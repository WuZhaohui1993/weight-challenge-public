package com.ruoyi.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 移动端用户设置更新请求
 */
public class UpdateUserSettingsRequest {

    private String nickname;

    private String avatar;

    private String gender;

    private BigDecimal height;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    private BigDecimal targetWeight;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date targetCompletionDate;

    private BigDecimal currentWeight;

    private Long dailyCalorieTarget;

    private Long dailyCalorieDeficitTarget;

    private Long dailyWaterTarget;

    private Long mainCircleId;

    private Integer circleFeatureEnabled;

    private String syncMode;

    private String phone;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public BigDecimal getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(BigDecimal targetWeight) {
        this.targetWeight = targetWeight;
    }

    public Date getTargetCompletionDate() {
        return targetCompletionDate;
    }

    public void setTargetCompletionDate(Date targetCompletionDate) {
        this.targetCompletionDate = targetCompletionDate;
    }

    public BigDecimal getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(BigDecimal currentWeight) {
        this.currentWeight = currentWeight;
    }

    public Long getDailyCalorieTarget() {
        return dailyCalorieTarget;
    }

    public void setDailyCalorieTarget(Long dailyCalorieTarget) {
        this.dailyCalorieTarget = dailyCalorieTarget;
    }

    public Long getDailyCalorieDeficitTarget() {
        return dailyCalorieDeficitTarget;
    }

    public void setDailyCalorieDeficitTarget(Long dailyCalorieDeficitTarget) {
        this.dailyCalorieDeficitTarget = dailyCalorieDeficitTarget;
    }

    public Long getDailyWaterTarget() {
        return dailyWaterTarget;
    }

    public void setDailyWaterTarget(Long dailyWaterTarget) {
        this.dailyWaterTarget = dailyWaterTarget;
    }

    public Long getMainCircleId() {
        return mainCircleId;
    }

    public void setMainCircleId(Long mainCircleId) {
        this.mainCircleId = mainCircleId;
    }

    public Integer getCircleFeatureEnabled() {
        return circleFeatureEnabled;
    }

    public void setCircleFeatureEnabled(Integer circleFeatureEnabled) {
        this.circleFeatureEnabled = circleFeatureEnabled;
    }

    public String getSyncMode() {
        return syncMode;
    }

    public void setSyncMode(String syncMode) {
        this.syncMode = syncMode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
