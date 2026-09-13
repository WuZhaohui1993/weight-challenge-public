package com.ruoyi.api.dto;

import com.ruoyi.weight.domain.WeightUser;

import java.math.BigDecimal;
import java.util.List;

/**
 * 移动端公开用户主页响应
 */
public class ApiUserPublicProfileResponse {

    private Long userId;

    private String nickname;

    private String avatar;

    private String gender;

    private BigDecimal currentWeight;

    private BigDecimal targetWeight;

    private Long streakDays;

    private Long achievementPoints;

    private Long joinedCircleCount;

    private Long mainCircleId;

    private String mainCircleName;

    private Boolean self;

    private List<ApiFeedResponse.FeedCard> recentPublicFeeds;

    public static ApiUserPublicProfileResponse fromWeightUser(WeightUser weightUser) {
        ApiUserPublicProfileResponse response = new ApiUserPublicProfileResponse();
        response.setUserId(weightUser.getUserId());
        response.setNickname(weightUser.getNickname());
        response.setAvatar(weightUser.getAvatar());
        response.setGender(weightUser.getGender());
        response.setCurrentWeight(weightUser.getCurrentWeight());
        response.setTargetWeight(weightUser.getTargetWeight());
        response.setStreakDays(weightUser.getStreakDays());
        response.setAchievementPoints(weightUser.getAchievementPoints());
        response.setMainCircleId(weightUser.getMainCircleId());
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

    public BigDecimal getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(BigDecimal currentWeight) {
        this.currentWeight = currentWeight;
    }

    public BigDecimal getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(BigDecimal targetWeight) {
        this.targetWeight = targetWeight;
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

    public Long getJoinedCircleCount() {
        return joinedCircleCount;
    }

    public void setJoinedCircleCount(Long joinedCircleCount) {
        this.joinedCircleCount = joinedCircleCount;
    }

    public Long getMainCircleId() {
        return mainCircleId;
    }

    public void setMainCircleId(Long mainCircleId) {
        this.mainCircleId = mainCircleId;
    }

    public String getMainCircleName() {
        return mainCircleName;
    }

    public void setMainCircleName(String mainCircleName) {
        this.mainCircleName = mainCircleName;
    }

    public Boolean getSelf() {
        return self;
    }

    public void setSelf(Boolean self) {
        this.self = self;
    }

    public List<ApiFeedResponse.FeedCard> getRecentPublicFeeds() {
        return recentPublicFeeds;
    }

    public void setRecentPublicFeeds(List<ApiFeedResponse.FeedCard> recentPublicFeeds) {
        this.recentPublicFeeds = recentPublicFeeds;
    }
}
