package com.ruoyi.api.dto;

/**
 * 微信登录响应
 */
public class WxLoginResponse {

    /** access token */
    private String accessToken;

    /** refresh token */
    private String refreshToken;

    /** access token 过期秒数 */
    private Long accessExpiresIn;

    /** refresh token 过期秒数 */
    private Long refreshExpiresIn;

    /** access token 过期时间戳（毫秒） */
    private Long accessExpiresAt;

    /** refresh token 过期时间戳（毫秒） */
    private Long refreshExpiresAt;

    /** 是否新用户 */
    private Boolean isNewUser;

    /** 用户信息 */
    private ApiUserProfileResponse user;

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

    public Boolean getIsNewUser() {
        return isNewUser;
    }

    public void setIsNewUser(Boolean isNewUser) {
        this.isNewUser = isNewUser;
    }

    public ApiUserProfileResponse getUser() {
        return user;
    }

    public void setUser(ApiUserProfileResponse user) {
        this.user = user;
    }
}
