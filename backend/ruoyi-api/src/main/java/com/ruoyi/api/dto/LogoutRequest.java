package com.ruoyi.api.dto;

/**
 * 小程序退出登录请求
 */
public class LogoutRequest {

    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
