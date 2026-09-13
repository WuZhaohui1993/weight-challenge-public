package com.ruoyi.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 移动端首页 dashboard 响应
 */
public class ApiDashboardResponse {

    private Summary summary;

    private List<WeightTrendItem> weightTrend;

    private MainCircle mainCircle;

    private Long unreadNotificationCount;

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    public List<WeightTrendItem> getWeightTrend() {
        return weightTrend;
    }

    public void setWeightTrend(List<WeightTrendItem> weightTrend) {
        this.weightTrend = weightTrend;
    }

    public MainCircle getMainCircle() {
        return mainCircle;
    }

    public void setMainCircle(MainCircle mainCircle) {
        this.mainCircle = mainCircle;
    }

    public Long getUnreadNotificationCount() {
        return unreadNotificationCount;
    }

    public void setUnreadNotificationCount(Long unreadNotificationCount) {
        this.unreadNotificationCount = unreadNotificationCount;
    }

    public static class Summary {

        private BigDecimal currentWeight;

        private BigDecimal bmi;

        private BigDecimal targetWeight;

        @JsonFormat(pattern = "yyyy-MM-dd")
        private Date targetCompletionDate;

        private Long calorieIntake;

        private Long calorieTarget;

        private Long dailyCalorieDeficitTarget;

        private Long calorieRemaining;

        private Long exerciseCalories;

        private Long exerciseMinutes;

        private Long waterCups;

        private Long waterTarget;

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

        public Long getCalorieIntake() {
            return calorieIntake;
        }

        public void setCalorieIntake(Long calorieIntake) {
            this.calorieIntake = calorieIntake;
        }

        public Long getCalorieTarget() {
            return calorieTarget;
        }

        public void setCalorieTarget(Long calorieTarget) {
            this.calorieTarget = calorieTarget;
        }

        public Long getDailyCalorieDeficitTarget() {
            return dailyCalorieDeficitTarget;
        }

        public void setDailyCalorieDeficitTarget(Long dailyCalorieDeficitTarget) {
            this.dailyCalorieDeficitTarget = dailyCalorieDeficitTarget;
        }

        public Long getCalorieRemaining() {
            return calorieRemaining;
        }

        public void setCalorieRemaining(Long calorieRemaining) {
            this.calorieRemaining = calorieRemaining;
        }

        public Long getExerciseCalories() {
            return exerciseCalories;
        }

        public void setExerciseCalories(Long exerciseCalories) {
            this.exerciseCalories = exerciseCalories;
        }

        public Long getExerciseMinutes() {
            return exerciseMinutes;
        }

        public void setExerciseMinutes(Long exerciseMinutes) {
            this.exerciseMinutes = exerciseMinutes;
        }

        public Long getWaterCups() {
            return waterCups;
        }

        public void setWaterCups(Long waterCups) {
            this.waterCups = waterCups;
        }

        public Long getWaterTarget() {
            return waterTarget;
        }

        public void setWaterTarget(Long waterTarget) {
            this.waterTarget = waterTarget;
        }
    }

    public static class WeightTrendItem {

        private String date;

        private BigDecimal weight;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public BigDecimal getWeight() {
            return weight;
        }

        public void setWeight(BigDecimal weight) {
            this.weight = weight;
        }
    }

    public static class MainCircle {

        private Long id;

        private String name;

        private List<CircleMemberItem> members;

        private List<CircleRankingItem> ranking;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<CircleMemberItem> getMembers() {
            return members;
        }

        public void setMembers(List<CircleMemberItem> members) {
            this.members = members;
        }

        public List<CircleRankingItem> getRanking() {
            return ranking;
        }

        public void setRanking(List<CircleRankingItem> ranking) {
            this.ranking = ranking;
        }
    }

    public static class CircleMemberItem {

        private Long userId;

        private String nickname;

        private String avatar;

        private String role;

        private Long streakDays;

        private BigDecimal weightChange;

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

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public Long getStreakDays() {
            return streakDays;
        }

        public void setStreakDays(Long streakDays) {
            this.streakDays = streakDays;
        }

        public BigDecimal getWeightChange() {
            return weightChange;
        }

        public void setWeightChange(BigDecimal weightChange) {
            this.weightChange = weightChange;
        }
    }

    public static class CircleRankingItem {

        private Integer rank;

        private Long userId;

        private String nickname;

        private String avatar;

        private BigDecimal weightChange;

        private Long completedTaskCount;

        private Long circleCheckins;

        public Integer getRank() {
            return rank;
        }

        public void setRank(Integer rank) {
            this.rank = rank;
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

        public BigDecimal getWeightChange() {
            return weightChange;
        }

        public void setWeightChange(BigDecimal weightChange) {
            this.weightChange = weightChange;
        }

        public Long getCompletedTaskCount() {
            return completedTaskCount;
        }

        public void setCompletedTaskCount(Long completedTaskCount) {
            this.completedTaskCount = completedTaskCount;
        }

        public Long getCircleCheckins() {
            return circleCheckins;
        }

        public void setCircleCheckins(Long circleCheckins) {
            this.circleCheckins = circleCheckins;
        }
    }
}
