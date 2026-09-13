package com.ruoyi.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.weight.domain.WeightUser;
import com.ruoyi.weight.support.WeightUserGoalCalculator;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 移动端当前用户信息响应
 */
public class ApiUserProfileResponse {

    private Long userId;

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

    private BigDecimal bmi;

    private Long dailyCalorieTarget;

    private Long dailyCalorieDeficitTarget;

    private Long dailyWaterTarget;

    private Long streakDays;

    private Long achievementPoints;

    private Long mainCircleId;

    private Integer circleFeatureEnabled;

    private String syncMode;

    private String source;

    private String phone;

    public static ApiUserProfileResponse fromWeightUser(WeightUser weightUser) {
        ApiUserProfileResponse response = new ApiUserProfileResponse();
        response.setUserId(weightUser.getUserId());
        response.setNickname(weightUser.getNickname());
        response.setAvatar(weightUser.getAvatar());
        response.setGender(weightUser.getGender());
        response.setHeight(weightUser.getHeight());
        response.setBirthday(weightUser.getBirthday());
        response.setTargetWeight(weightUser.getTargetWeight());
        response.setTargetCompletionDate(weightUser.getTargetCompletionDate());
        response.setCurrentWeight(weightUser.getCurrentWeight());
        response.setBmi(weightUser.getBmi());
        response.setDailyCalorieTarget(WeightUserGoalCalculator.calculateDailyCalorieTarget(weightUser));
        response.setDailyCalorieDeficitTarget(WeightUserGoalCalculator.calculateDailyCalorieDeficit(weightUser));
        response.setDailyWaterTarget(weightUser.getDailyWaterTarget());
        response.setStreakDays(weightUser.getStreakDays());
        response.setAchievementPoints(weightUser.getAchievementPoints());
        response.setMainCircleId(weightUser.getMainCircleId());
        response.setCircleFeatureEnabled(weightUser.getCircleFeatureEnabled());
        response.setSyncMode(weightUser.getSyncMode());
        response.setSource(weightUser.getSource());
        response.setPhone(weightUser.getPhone());
        return response;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

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

    public BigDecimal getBmi() {
        return bmi;
    }

    public void setBmi(BigDecimal bmi) {
        this.bmi = bmi;
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

    public Long getStreakDays() {
        return streakDays;
    }

    public void setStreakDays(Long streakDays) {
        this.streakDays = streakDays;
    }

    public Long getAchievementPoints() {
        return achievementPoints;
    }

    public void setAchievementPoints(Long achievementPoints) {
        this.achievementPoints = achievementPoints;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
