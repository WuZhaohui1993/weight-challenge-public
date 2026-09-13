package com.ruoyi.api.security;

import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.weight.domain.WeightCircle;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * 私密圈子邀请 token 服务
 */
@Component
public class CircleInviteTokenService {

    private static final String CACHE_PREFIX = "miniapp:circle:invite:";
    private static final String ACTIVE_CACHE_PREFIX = "miniapp:circle:invite:active:";

    @Value("${miniapp.circle.invite.expireDays:7}")
    private int inviteExpireDays;

    @Autowired
    private RedisCache redisCache;

    public InviteSession createInvite(WeightCircle circle, Long inviterUserId, boolean approvalRequired) {
        InviteSession existing = getCurrentInvite(circle != null ? circle.getId() : null, inviterUserId, approvalRequired);
        if (existing != null) {
            return existing;
        }
        return issueInvite(circle, inviterUserId, approvalRequired);
    }

    public InviteSession getCurrentInvite(Long circleId, Long inviterUserId, boolean approvalRequired) {
        if (circleId == null || inviterUserId == null) {
            return null;
        }
        String activeToken = redisCache.getCacheObject(buildActiveCacheKey(circleId, inviterUserId, approvalRequired));
        if (StringUtils.isBlank(activeToken)) {
            return null;
        }
        InviteSession session = redisCache.getCacheObject(buildCacheKey(activeToken));
        if (session == null) {
            redisCache.deleteObject(buildActiveCacheKey(circleId, inviterUserId, approvalRequired));
            return null;
        }
        return session;
    }

    private InviteSession issueInvite(WeightCircle circle, Long inviterUserId, boolean approvalRequired) {
        String token = generateToken();
        InviteSession session = new InviteSession();
        session.setToken(token);
        session.setCircleId(circle.getId());
        session.setCircleName(circle.getName());
        session.setCircleType(circle.getType());
        session.setInviterUserId(inviterUserId);
        session.setApprovalRequired(approvalRequired);
        session.setExpiresAt(System.currentTimeMillis() + inviteExpireDays * 24L * 60L * 60L * 1000L);
        redisCache.setCacheObject(buildCacheKey(token), session, inviteExpireDays, TimeUnit.DAYS);
        redisCache.setCacheObject(buildActiveCacheKey(circle.getId(), inviterUserId, approvalRequired), token, inviteExpireDays, TimeUnit.DAYS);
        return session;
    }

    public InviteSession resolveInvite(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        InviteSession session = redisCache.getCacheObject(buildCacheKey(token.trim()));
        if (session == null) {
            return null;
        }
        String activeToken = redisCache.getCacheObject(buildActiveCacheKey(
                session.getCircleId(),
                session.getInviterUserId(),
                Boolean.TRUE.equals(session.getApprovalRequired())
        ));
        String legacyActiveToken = redisCache.getCacheObject(buildLegacyActiveCacheKey(session.getCircleId()));
        if ((StringUtils.isBlank(activeToken) || !StringUtils.equals(activeToken, token.trim()))
                && (StringUtils.isBlank(legacyActiveToken) || !StringUtils.equals(legacyActiveToken, token.trim()))) {
            redisCache.deleteObject(buildCacheKey(token.trim()));
            return null;
        }
        return session;
    }

    private String generateToken() {
        return IdUtils.fastUUID().replace("-", "").substring(0, 16).toUpperCase(Locale.ROOT);
    }

    private String buildCacheKey(String token) {
        return CACHE_PREFIX + token;
    }

    private String buildActiveCacheKey(Long circleId, Long inviterUserId, boolean approvalRequired) {
        return ACTIVE_CACHE_PREFIX + circleId + ":" + inviterUserId + ":" + (approvalRequired ? "approval" : "direct");
    }

    private String buildLegacyActiveCacheKey(Long circleId) {
        return ACTIVE_CACHE_PREFIX + circleId;
    }

    public static class InviteSession implements Serializable {
        private static final long serialVersionUID = 1L;

        private String token;
        private Long circleId;
        private String circleName;
        private String circleType;
        private Long inviterUserId;
        private Boolean approvalRequired;
        private Long expiresAt;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public Long getCircleId() {
            return circleId;
        }

        public void setCircleId(Long circleId) {
            this.circleId = circleId;
        }

        public String getCircleName() {
            return circleName;
        }

        public void setCircleName(String circleName) {
            this.circleName = circleName;
        }

        public String getCircleType() {
            return circleType;
        }

        public void setCircleType(String circleType) {
            this.circleType = circleType;
        }

        public Long getInviterUserId() {
            return inviterUserId;
        }

        public void setInviterUserId(Long inviterUserId) {
            this.inviterUserId = inviterUserId;
        }

        public Boolean getApprovalRequired() {
            return approvalRequired;
        }

        public void setApprovalRequired(Boolean approvalRequired) {
            this.approvalRequired = approvalRequired;
        }

        public Long getExpiresAt() {
            return expiresAt;
        }

        public void setExpiresAt(Long expiresAt) {
            this.expiresAt = expiresAt;
        }
    }
}
