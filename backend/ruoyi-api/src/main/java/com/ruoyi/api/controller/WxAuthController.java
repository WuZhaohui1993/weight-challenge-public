package com.ruoyi.api.controller;

import com.ruoyi.api.dto.ApiUserProfileResponse;
import com.ruoyi.api.dto.LogoutRequest;
import com.ruoyi.api.dto.RefreshTokenRequest;
import com.ruoyi.api.dto.TokenRefreshResponse;
import com.ruoyi.api.dto.WxLoginRequest;
import com.ruoyi.api.dto.WxLoginResponse;
import com.ruoyi.api.security.MiniAppTokenService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.weight.domain.WeightUser;
import com.ruoyi.weight.service.IWeightUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;

import java.util.List;

/**
 * 微信小程序登录接口
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@RestController
@RequestMapping("/api/auth")
public class WxAuthController {

    private static final Logger log = LoggerFactory.getLogger(WxAuthController.class);

    @Autowired(required = false)
    private WxMaService wxMaService;

    @Value("${wx.miniapp.mock-login-enabled:false}")
    private boolean mockLoginEnabled;

    @Autowired
    private IWeightUserService weightUserService;

    @Autowired
    private MiniAppTokenService miniAppTokenService;

    /**
     * 微信小程序登录
     * 
     * @param request 包含 code 的登录请求
     * @return 登录结果，包含双 token 和用户信息
     */
    @PostMapping("/wxLogin")
    public AjaxResult wxLogin(@RequestBody WxLoginRequest request) {
        try {
            if (request == null || StringUtils.isEmpty(request.getCode())) {
                return AjaxResult.error("登录失败：缺少微信登录 code");
            }

            if (wxMaService == null) {
                if (!mockLoginEnabled) {
                    log.error("微信小程序服务未配置，拒绝登录。请配置 wx.miniapp.appid 和 wx.miniapp.secret");
                    return AjaxResult.error("服务器未配置微信登录，请配置 wx.miniapp.appid 和 wx.miniapp.secret");
                }
                log.warn("微信小程序服务未配置，使用模拟登录");
                return mockLogin(request);
            }

            // 调用微信接口获取 openid 和 session_key
            WxMaJscode2SessionResult session = wxMaService.getUserService()
                    .getSessionInfo(request.getCode());

            String openid = session.getOpenid();
            String unionid = session.getUnionid();

            UserUpsertResult userResult = getOrCreateUser(openid, unionid, request);
            WeightUser weightUser = userResult.getWeightUser();

            MiniAppTokenService.TokenPair tokenPair = miniAppTokenService.createTokenPair(weightUser);
            return AjaxResult.success(buildLoginResponse(tokenPair, weightUser, userResult.getIsNewUser()));
        } catch (Exception e) {
            log.error("微信登录失败", e);
            return AjaxResult.error("登录失败：" + e.getMessage());
        }
    }
    
    /**
     * 从 openid 生成用户 ID（简化实现）
     */
    private Long generateUserIdFromOpenid(String openid) {
        // 实际项目需要维护 openid -> userId 的映射表
        return (long) Math.abs(openid.hashCode());
    }

    /**
     * 模拟登录（开发测试用）
     */
    private AjaxResult mockLogin(WxLoginRequest request) {
        // 模拟用户 ID
        Long mockUserId = 100001L;

        UserUpsertResult userResult = getOrCreateMockUser(mockUserId, request);
        WeightUser weightUser = userResult.getWeightUser();
        MiniAppTokenService.TokenPair tokenPair = miniAppTokenService.createTokenPair(weightUser);
        return AjaxResult.success(buildLoginResponse(tokenPair, weightUser, userResult.getIsNewUser()));
    }

    /**
     * 获取或创建用户
     */
    private UserUpsertResult getOrCreateUser(String openid, String unionid, WxLoginRequest request) {
        WeightUser weightUser = findByOpenid(openid);
        boolean isNewUser = false;

        if (weightUser == null) {
            isNewUser = true;
            weightUser = new WeightUser();
            weightUser.setUserId(generateUserIdFromOpenid(openid));
            weightUser.setSource("0");
            weightUser.setOpenid(openid);
            weightUser.setUnionid(unionid);
            applyUserProfile(weightUser, request);
            weightUserService.insertWeightUser(weightUser);
            weightUser = weightUserService.selectWeightUserByUserId(weightUser.getUserId());
        } else {
            weightUser.setOpenid(openid);
            weightUser.setUnionid(unionid);
            applyUserProfile(weightUser, request);
            weightUserService.updateWeightUser(weightUser);
            weightUser = weightUserService.selectWeightUserByUserId(weightUser.getUserId());
        }

        return new UserUpsertResult(weightUser, isNewUser);
    }

    private UserUpsertResult getOrCreateMockUser(Long userId, WxLoginRequest request) {
        WeightUser weightUser = weightUserService.selectWeightUserByUserId(userId);
        boolean isNewUser = false;

        if (weightUser == null) {
            isNewUser = true;
            weightUser = new WeightUser();
            weightUser.setUserId(userId);
            weightUser.setSource("0");
            applyUserProfile(weightUser, request);
            weightUserService.insertWeightUser(weightUser);
            weightUser = weightUserService.selectWeightUserByUserId(weightUser.getUserId());
        } else {
            applyUserProfile(weightUser, request);
            weightUserService.updateWeightUser(weightUser);
            weightUser = weightUserService.selectWeightUserByUserId(weightUser.getUserId());
        }

        return new UserUpsertResult(weightUser, isNewUser);
    }

    private WeightUser findByOpenid(String openid) {
        if (StringUtils.isEmpty(openid)) {
            return null;
        }

        WeightUser query = new WeightUser();
        query.setOpenid(openid);
        List<WeightUser> users = weightUserService.selectWeightUserList(query);
        if (users == null || users.isEmpty()) {
            return null;
        }
        return users.get(0);
    }

    private void applyUserProfile(WeightUser weightUser, WxLoginRequest request) {
        if (StringUtils.isNotEmpty(request.getNickname())) {
            weightUser.setNickname(request.getNickname());
        } else if (StringUtils.isEmpty(weightUser.getNickname())) {
            weightUser.setNickname("微信用户");
        }

        if (request.getAvatarUrl() != null) {
            weightUser.setAvatar(request.getAvatarUrl());
        }
        if (request.getGender() != null) {
            weightUser.setGender(String.valueOf(request.getGender()));
        } else if (StringUtils.isEmpty(weightUser.getGender())) {
            weightUser.setGender("0");
        }
    }

    /**
     * 刷新 Token
     */
    @PostMapping("/refreshToken")
    public AjaxResult refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            if (request == null || StringUtils.isEmpty(request.getRefreshToken())) {
                return AjaxResult.error("refreshToken 不能为空");
            }

            MiniAppTokenService.TokenPair tokenPair = miniAppTokenService.refreshTokenPair(request.getRefreshToken());
            TokenRefreshResponse response = new TokenRefreshResponse();
            response.setAccessToken(tokenPair.getAccessToken());
            response.setRefreshToken(tokenPair.getRefreshToken());
            response.setAccessExpiresIn(tokenPair.getAccessExpiresIn());
            response.setRefreshExpiresIn(tokenPair.getRefreshExpiresIn());
            response.setAccessExpiresAt(tokenPair.getAccessExpiresAt());
            response.setRefreshExpiresAt(tokenPair.getRefreshExpiresAt());
            return AjaxResult.success("刷新成功", response);
        } catch (Exception e) {
            log.error("刷新Token失败", e);
            return AjaxResult.error("Token无效或已过期");
        }
    }

    @PostMapping("/logout")
    public AjaxResult logout(@RequestBody(required = false) LogoutRequest request) {
        if (request != null) {
            miniAppTokenService.revokeRefreshToken(request.getRefreshToken());
        }
        return AjaxResult.success("退出成功");
    }

    private WxLoginResponse buildLoginResponse(MiniAppTokenService.TokenPair tokenPair, WeightUser weightUser, boolean isNewUser) {
        WxLoginResponse response = new WxLoginResponse();
        response.setAccessToken(tokenPair.getAccessToken());
        response.setRefreshToken(tokenPair.getRefreshToken());
        response.setAccessExpiresIn(tokenPair.getAccessExpiresIn());
        response.setRefreshExpiresIn(tokenPair.getRefreshExpiresIn());
        response.setAccessExpiresAt(tokenPair.getAccessExpiresAt());
        response.setRefreshExpiresAt(tokenPair.getRefreshExpiresAt());
        response.setIsNewUser(isNewUser);
        response.setUser(ApiUserProfileResponse.fromWeightUser(weightUser));
        return response;
    }

    private static class UserUpsertResult {
        private final WeightUser weightUser;
        private final boolean isNewUser;

        private UserUpsertResult(WeightUser weightUser, boolean isNewUser) {
            this.weightUser = weightUser;
            this.isNewUser = isNewUser;
        }

        public WeightUser getWeightUser() {
            return weightUser;
        }

        public boolean getIsNewUser() {
            return isNewUser;
        }
    }
}
