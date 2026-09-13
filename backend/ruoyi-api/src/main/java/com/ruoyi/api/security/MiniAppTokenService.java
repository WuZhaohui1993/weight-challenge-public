package com.ruoyi.api.security;

import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.weight.domain.WeightUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 小程序双 token 服务
 */
@Component
public class MiniAppTokenService {

    private static final String TOKEN_TYPE_KEY = "tokenType";
    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";
    private static final String REFRESH_TOKEN_ID_KEY = "refreshTokenId";
    private static final String REFRESH_TOKEN_CACHE_PREFIX = "miniapp:refresh:";
    private static final long SECONDS_PER_MINUTE = 60L;
    private static final long SECONDS_PER_DAY = 24L * 60L * 60L;

    @Value("${token.secret:abcdefghijklmnopqrstuvwxyz}")
    private String secret;

    @Value("${miniapp.auth.accessExpireMinutes:120}")
    private int accessExpireMinutes;

    @Value("${miniapp.auth.refreshExpireDays:30}")
    private int refreshExpireDays;

    @Autowired
    private RedisCache redisCache;

    public TokenPair createTokenPair(WeightUser user) {
        return issueTokenPair(user, null);
    }

    public TokenPair refreshTokenPair(String refreshToken) {
        Claims claims = parseRefreshClaims(refreshToken);
        String refreshTokenId = String.valueOf(claims.get(REFRESH_TOKEN_ID_KEY));
        RefreshSession session = redisCache.getCacheObject(buildRefreshTokenCacheKey(refreshTokenId));
        if (session == null) {
            throw new IllegalStateException("refreshToken 已失效");
        }

        Long userId = Long.valueOf(String.valueOf(claims.get("userId")));
        if (!userId.equals(session.getUserId())) {
            redisCache.deleteObject(buildRefreshTokenCacheKey(refreshTokenId));
            throw new IllegalStateException("refreshToken 用户不匹配");
        }

        WeightUser user = new WeightUser();
        user.setUserId(session.getUserId());
        user.setNickname(session.getNickname());
        user.setOpenid(session.getOpenid());
        return issueTokenPair(user, refreshTokenId);
    }

    public void revokeRefreshToken(String refreshToken) {
        if (StringUtils.isEmpty(refreshToken)) {
            return;
        }
        try {
            Claims claims = parseRefreshClaims(refreshToken);
            String refreshTokenId = String.valueOf(claims.get(REFRESH_TOKEN_ID_KEY));
            redisCache.deleteObject(buildRefreshTokenCacheKey(refreshTokenId));
        } catch (Exception ignored) {
        }
    }

    public Claims parseAccessToken(String accessToken) {
        Claims claims = parseToken(accessToken);
        if (!TOKEN_TYPE_ACCESS.equals(claims.get(TOKEN_TYPE_KEY))) {
            throw new IllegalArgumentException("非法 access token");
        }
        return claims;
    }

    private TokenPair issueTokenPair(WeightUser user, String oldRefreshTokenId) {
        String accessToken = createAccessToken(user);
        String refreshTokenId = IdUtils.fastUUID();
        String refreshToken = createRefreshToken(user, refreshTokenId);
        long now = System.currentTimeMillis();
        long accessExpiresAt = now + accessExpireMinutes * 60L * 1000L;
        long refreshExpiresAt = now + refreshExpireDays * 24L * 60L * 60L * 1000L;

        RefreshSession session = new RefreshSession();
        session.setUserId(user.getUserId());
        session.setOpenid(user.getOpenid());
        session.setNickname(user.getNickname());
        session.setExpiresAt(refreshExpiresAt);
        redisCache.setCacheObject(
                buildRefreshTokenCacheKey(refreshTokenId),
                session,
                refreshExpireDays,
                TimeUnit.DAYS
        );

        if (StringUtils.isNotEmpty(oldRefreshTokenId)) {
            redisCache.deleteObject(buildRefreshTokenCacheKey(oldRefreshTokenId));
        }

        TokenPair pair = new TokenPair();
        pair.setAccessToken(accessToken);
        pair.setRefreshToken(refreshToken);
        pair.setAccessExpiresIn(accessExpireMinutes * SECONDS_PER_MINUTE);
        pair.setRefreshExpiresIn(refreshExpireDays * SECONDS_PER_DAY);
        pair.setAccessExpiresAt(accessExpiresAt);
        pair.setRefreshExpiresAt(refreshExpiresAt);
        return pair;
    }

    private String createAccessToken(WeightUser user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(TOKEN_TYPE_KEY, TOKEN_TYPE_ACCESS);
        claims.put("userId", user.getUserId());
        claims.put("nickname", user.getNickname());
        if (StringUtils.isNotEmpty(user.getOpenid())) {
            claims.put("openid", user.getOpenid());
        }

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(user.getUserId()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpireMinutes * 60L * 1000L))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    private String createRefreshToken(WeightUser user, String refreshTokenId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(TOKEN_TYPE_KEY, TOKEN_TYPE_REFRESH);
        claims.put(REFRESH_TOKEN_ID_KEY, refreshTokenId);
        claims.put("userId", user.getUserId());
        if (StringUtils.isNotEmpty(user.getOpenid())) {
            claims.put("openid", user.getOpenid());
        }

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(user.getUserId()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpireDays * 24L * 60L * 60L * 1000L))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    private Claims parseRefreshClaims(String refreshToken) {
        Claims claims = parseToken(refreshToken);
        if (!TOKEN_TYPE_REFRESH.equals(claims.get(TOKEN_TYPE_KEY))) {
            throw new IllegalArgumentException("非法 refresh token");
        }
        if (claims.get(REFRESH_TOKEN_ID_KEY) == null) {
            throw new IllegalArgumentException("refresh token 缺少标识");
        }
        return claims;
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    private String buildRefreshTokenCacheKey(String refreshTokenId) {
        return REFRESH_TOKEN_CACHE_PREFIX + refreshTokenId;
    }

    public static class TokenPair {
        private String accessToken;
        private String refreshToken;
        private Long accessExpiresIn;
        private Long refreshExpiresIn;
        private Long accessExpiresAt;
        private Long refreshExpiresAt;

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        public Long getAccessExpiresIn() {
            return accessExpiresIn;
        }

        public void setAccessExpiresIn(Long accessExpiresIn) {
            this.accessExpiresIn = accessExpiresIn;
        }

        public Long getRefreshExpiresIn() {
            return refreshExpiresIn;
        }

        public void setRefreshExpiresIn(Long refreshExpiresIn) {
            this.refreshExpiresIn = refreshExpiresIn;
        }

        public Long getAccessExpiresAt() {
            return accessExpiresAt;
        }

        public void setAccessExpiresAt(Long accessExpiresAt) {
            this.accessExpiresAt = accessExpiresAt;
        }

        public Long getRefreshExpiresAt() {
            return refreshExpiresAt;
        }

        public void setRefreshExpiresAt(Long refreshExpiresAt) {
            this.refreshExpiresAt = refreshExpiresAt;
        }
    }

    public static class RefreshSession implements Serializable {
        private static final long serialVersionUID = 1L;

        private Long userId;
        private String openid;
        private String nickname;
        private Long expiresAt;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getOpenid() {
            return openid;
        }

        public void setOpenid(String openid) {
            this.openid = openid;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public Long getExpiresAt() {
            return expiresAt;
        }

        public void setExpiresAt(Long expiresAt) {
            this.expiresAt = expiresAt;
        }
    }
}
