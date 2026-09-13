package com.ruoyi.api.dto;

/**
 * 刷新 token 响应
 */
public class TokenRefreshResponse {

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
