package com.ruoyi.api.dto;

/**
 * 微信登录请求
 */
public class WxLoginRequest {

    /** 微信登录 code */
    private String code;

    /** 用户昵称 */
    private String nickname;

    /** 用户头像 */
    private String avatarUrl;

    /** 性别 0-未知 1-男 2-女 */
    private Long gender;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Long getGender() {
        return gender;
    }

    public void setGender(Long gender) {
        this.gender = gender;
    }
}
