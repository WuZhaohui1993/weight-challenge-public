package com.ruoyi.api.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.weight.domain.WeightAchievement;

/**
 * 移动端成就徽章响应。
 */
public class ApiAchievementResponse {
    private Long id;
    private String badgeType;
    private String badgeName;
    private String badgeIcon;
    private String badgeDescription;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date unlockedAt;

    private BigDecimal progress;
    private Boolean unlocked;

    public static ApiAchievementResponse fromWeightAchievement(WeightAchievement achievement) {
        ApiAchievementResponse response = new ApiAchievementResponse();
        response.setId(achievement.getId());
        response.setBadgeType(achievement.getBadgeType());
        response.setBadgeName(achievement.getBadgeName());
        response.setBadgeIcon(achievement.getBadgeIcon());
        response.setBadgeDescription(achievement.getBadgeDescription());
        response.setUnlockedAt(achievement.getUnlockedAt());
        response.setProgress(achievement.getProgress());
        response.setUnlocked(Integer.valueOf(1).equals(achievement.getIsUnlocked()));
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBadgeType() {
        return badgeType;
    }

    public void setBadgeType(String badgeType) {
        this.badgeType = badgeType;
    }

    public String getBadgeName() {
        return badgeName;
    }

    public void setBadgeName(String badgeName) {
        this.badgeName = badgeName;
    }

    public String getBadgeIcon() {
        return badgeIcon;
    }

    public void setBadgeIcon(String badgeIcon) {
        this.badgeIcon = badgeIcon;
    }

    public String getBadgeDescription() {
        return badgeDescription;
    }

    public void setBadgeDescription(String badgeDescription) {
        this.badgeDescription = badgeDescription;
    }

    public Date getUnlockedAt() {
        return unlockedAt;
    }

    public void setUnlockedAt(Date unlockedAt) {
        this.unlockedAt = unlockedAt;
    }

    public BigDecimal getProgress() {
        return progress;
    }

    public void setProgress(BigDecimal progress) {
        this.progress = progress;
    }

    public Boolean getUnlocked() {
        return unlocked;
    }

    public void setUnlocked(Boolean unlocked) {
        this.unlocked = unlocked;
    }
}
