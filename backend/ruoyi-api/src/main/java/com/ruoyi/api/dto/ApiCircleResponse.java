package com.ruoyi.api.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 移动端圈子相关响应 DTO
 */
public class ApiCircleResponse {

    public static class CircleCategoryOption {

        private Long id;

        private String code;

        private String name;

        private String icon;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }

    public static class CircleCard {

        private Long id;

        private String name;

        private Long categoryId;

        private String icon;

        private String coverUrl;

        private String type;

        private String description;

        private Long memberCount;

        private BigDecimal depositRequired;

        private Long durationDays;

        private String startDate;

        private String endDate;

        private String categoryName;

        private String status;

        private Boolean joined;

        private String lifecycleStatus;

        private Boolean readOnly;

        private String readOnlyReason;

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

        public Long getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(Long categoryId) {
            this.categoryId = categoryId;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getCoverUrl() {
            return coverUrl;
        }

        public void setCoverUrl(String coverUrl) {
            this.coverUrl = coverUrl;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Long getMemberCount() {
            return memberCount;
        }

        public void setMemberCount(Long memberCount) {
            this.memberCount = memberCount;
        }

        public BigDecimal getDepositRequired() {
            return depositRequired;
        }

        public void setDepositRequired(BigDecimal depositRequired) {
            this.depositRequired = depositRequired;
        }

        public Long getDurationDays() {
            return durationDays;
        }

        public void setDurationDays(Long durationDays) {
            this.durationDays = durationDays;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Boolean getJoined() {
            return joined;
        }

        public void setJoined(Boolean joined) {
            this.joined = joined;
        }

        public String getLifecycleStatus() {
            return lifecycleStatus;
        }

        public void setLifecycleStatus(String lifecycleStatus) {
            this.lifecycleStatus = lifecycleStatus;
        }

        public Boolean getReadOnly() {
            return readOnly;
        }

        public void setReadOnly(Boolean readOnly) {
            this.readOnly = readOnly;
        }

        public String getReadOnlyReason() {
            return readOnlyReason;
        }

        public void setReadOnlyReason(String readOnlyReason) {
            this.readOnlyReason = readOnlyReason;
        }
    }

    public static class MyCircleCard extends CircleCard {

        private String role;

        private String joinedAt;

        private Long streakDays;

        private Long totalCheckins;

        private String depositStatus;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getJoinedAt() {
            return joinedAt;
        }

        public void setJoinedAt(String joinedAt) {
            this.joinedAt = joinedAt;
        }

        public Long getStreakDays() {
            return streakDays;
        }

        public void setStreakDays(Long streakDays) {
            this.streakDays = streakDays;
        }

        public Long getTotalCheckins() {
            return totalCheckins;
        }

        public void setTotalCheckins(Long totalCheckins) {
            this.totalCheckins = totalCheckins;
        }

        public String getDepositStatus() {
            return depositStatus;
        }

        public void setDepositStatus(String depositStatus) {
            this.depositStatus = depositStatus;
        }
    }

    public static class CircleFeedCard {

        private Long id;

        private Long circleId;

        private String circleName;

        private Long userId;

        private String userNickname;

        private String userAvatar;

        private String feedType;

        private String content;

        private List<String> images;

        private Long likesCount;

        private Long commentsCount;

        private Boolean isFeatured;

        private String createdAt;

        private String visibilityScope;

        private Long originCircleId;

        private String originCircleName;

        private List<Long> syncCircleIds;

        private Boolean commentEnabled;

        private Boolean ownedByMe;

        private String sourceType;

        private Long sourceId;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getCircleId() {
            return circleId;
        }

        public void setCircleId(Long circleId) {
            this.circleId = circleId;
        }

        public String getCircleName() {
            return circleName;
        }

        public void setCircleName(String circleName) {
            this.circleName = circleName;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getUserNickname() {
            return userNickname;
        }

        public void setUserNickname(String userNickname) {
            this.userNickname = userNickname;
        }

        public String getUserAvatar() {
            return userAvatar;
        }

        public void setUserAvatar(String userAvatar) {
            this.userAvatar = userAvatar;
        }

        public String getFeedType() {
            return feedType;
        }

        public void setFeedType(String feedType) {
            this.feedType = feedType;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }

        public Long getLikesCount() {
            return likesCount;
        }

        public void setLikesCount(Long likesCount) {
            this.likesCount = likesCount;
        }

        public Long getCommentsCount() {
            return commentsCount;
        }

        public void setCommentsCount(Long commentsCount) {
            this.commentsCount = commentsCount;
        }

        public Boolean getIsFeatured() {
            return isFeatured;
        }

        public void setIsFeatured(Boolean isFeatured) {
            this.isFeatured = isFeatured;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getVisibilityScope() {
            return visibilityScope;
        }

        public void setVisibilityScope(String visibilityScope) {
            this.visibilityScope = visibilityScope;
        }

        public Long getOriginCircleId() {
            return originCircleId;
        }

        public void setOriginCircleId(Long originCircleId) {
            this.originCircleId = originCircleId;
        }

        public String getOriginCircleName() {
            return originCircleName;
        }

        public void setOriginCircleName(String originCircleName) {
            this.originCircleName = originCircleName;
        }

        public List<Long> getSyncCircleIds() {
            return syncCircleIds;
        }

        public void setSyncCircleIds(List<Long> syncCircleIds) {
            this.syncCircleIds = syncCircleIds;
        }

        public Boolean getCommentEnabled() {
            return commentEnabled;
        }

        public void setCommentEnabled(Boolean commentEnabled) {
            this.commentEnabled = commentEnabled;
        }

        public Boolean getOwnedByMe() {
            return ownedByMe;
        }

        public void setOwnedByMe(Boolean ownedByMe) {
            this.ownedByMe = ownedByMe;
        }

        public String getSourceType() {
            return sourceType;
        }

        public void setSourceType(String sourceType) {
            this.sourceType = sourceType;
        }

        public Long getSourceId() {
            return sourceId;
        }

        public void setSourceId(Long sourceId) {
            this.sourceId = sourceId;
        }
    }

    public static class CirclePreviewMember {

        private Long userId;

        private String nickname;

        private String avatar;

        private String role;

        private Long streakDays;

        private Long totalCheckins;

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

        public Long getTotalCheckins() {
            return totalCheckins;
        }

        public void setTotalCheckins(Long totalCheckins) {
            this.totalCheckins = totalCheckins;
        }
    }

    public static class CircleGoalItem {

        private Long id;

        private String goalType;

        private String goalName;

        private String description;

        private String metricCode;

        private BigDecimal targetValue;

        private String targetUnit;

        private String period;

        private String periodLabel;

        private String verificationType;

        private String verificationTypeLabel;

        private String penaltyForFailure;

        private Boolean required;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getGoalType() {
            return goalType;
        }

        public void setGoalType(String goalType) {
            this.goalType = goalType;
        }

        public String getGoalName() {
            return goalName;
        }

        public void setGoalName(String goalName) {
            this.goalName = goalName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getMetricCode() {
            return metricCode;
        }

        public void setMetricCode(String metricCode) {
            this.metricCode = metricCode;
        }

        public BigDecimal getTargetValue() {
            return targetValue;
        }

        public void setTargetValue(BigDecimal targetValue) {
            this.targetValue = targetValue;
        }

        public String getTargetUnit() {
            return targetUnit;
        }

        public void setTargetUnit(String targetUnit) {
            this.targetUnit = targetUnit;
        }

        public String getPeriod() {
            return period;
        }

        public void setPeriod(String period) {
            this.period = period;
        }

        public String getPeriodLabel() {
            return periodLabel;
        }

        public void setPeriodLabel(String periodLabel) {
            this.periodLabel = periodLabel;
        }

        public String getVerificationType() {
            return verificationType;
        }

        public void setVerificationType(String verificationType) {
            this.verificationType = verificationType;
        }

        public String getVerificationTypeLabel() {
            return verificationTypeLabel;
        }

        public void setVerificationTypeLabel(String verificationTypeLabel) {
            this.verificationTypeLabel = verificationTypeLabel;
        }

        public String getPenaltyForFailure() {
            return penaltyForFailure;
        }

        public void setPenaltyForFailure(String penaltyForFailure) {
            this.penaltyForFailure = penaltyForFailure;
        }

        public Boolean getRequired() {
            return required;
        }

        public void setRequired(Boolean required) {
            this.required = required;
        }
    }

    public static class CircleRankingPreviewItem {

        private Long rankNum;

        private Long userId;

        private String nickname;

        private String avatar;

        private BigDecimal score;

        public Long getRankNum() {
            return rankNum;
        }

        public void setRankNum(Long rankNum) {
            this.rankNum = rankNum;
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

        public BigDecimal getScore() {
            return score;
        }

        public void setScore(BigDecimal score) {
            this.score = score;
        }
    }

    public static class CircleTaskItem {

        private Long id;

        private Long goalId;

        private String goalName;

        private String description;

        private String metricCode;

        private BigDecimal targetValue;

        private BigDecimal currentValue;

        private String targetUnit;

        private String period;

        private String periodLabel;

        private String verificationType;

        private String verificationTypeLabel;

        private Boolean completed;

        private Boolean manual;

        private Boolean readOnly;

        private String readOnlyReason;

        private String completedAt;

        private Boolean pendingPenalty;

        private String penaltyRuleText;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getGoalId() {
            return goalId;
        }

        public void setGoalId(Long goalId) {
            this.goalId = goalId;
        }

        public String getGoalName() {
            return goalName;
        }

        public void setGoalName(String goalName) {
            this.goalName = goalName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getMetricCode() {
            return metricCode;
        }

        public void setMetricCode(String metricCode) {
            this.metricCode = metricCode;
        }

        public BigDecimal getTargetValue() {
            return targetValue;
        }

        public void setTargetValue(BigDecimal targetValue) {
            this.targetValue = targetValue;
        }

        public BigDecimal getCurrentValue() {
            return currentValue;
        }

        public void setCurrentValue(BigDecimal currentValue) {
            this.currentValue = currentValue;
        }

        public String getTargetUnit() {
            return targetUnit;
        }

        public void setTargetUnit(String targetUnit) {
            this.targetUnit = targetUnit;
        }

        public String getPeriod() {
            return period;
        }

        public void setPeriod(String period) {
            this.period = period;
        }

        public String getPeriodLabel() {
            return periodLabel;
        }

        public void setPeriodLabel(String periodLabel) {
            this.periodLabel = periodLabel;
        }

        public String getVerificationType() {
            return verificationType;
        }

        public void setVerificationType(String verificationType) {
            this.verificationType = verificationType;
        }

        public String getVerificationTypeLabel() {
            return verificationTypeLabel;
        }

        public void setVerificationTypeLabel(String verificationTypeLabel) {
            this.verificationTypeLabel = verificationTypeLabel;
        }

        public Boolean getCompleted() {
            return completed;
        }

        public void setCompleted(Boolean completed) {
            this.completed = completed;
        }

        public Boolean getManual() {
            return manual;
        }

        public void setManual(Boolean manual) {
            this.manual = manual;
        }

        public Boolean getReadOnly() {
            return readOnly;
        }

        public void setReadOnly(Boolean readOnly) {
            this.readOnly = readOnly;
        }

        public String getReadOnlyReason() {
            return readOnlyReason;
        }

        public void setReadOnlyReason(String readOnlyReason) {
            this.readOnlyReason = readOnlyReason;
        }

        public String getCompletedAt() {
            return completedAt;
        }

        public void setCompletedAt(String completedAt) {
            this.completedAt = completedAt;
        }

        public Boolean getPendingPenalty() {
            return pendingPenalty;
        }

        public void setPendingPenalty(Boolean pendingPenalty) {
            this.pendingPenalty = pendingPenalty;
        }

        public String getPenaltyRuleText() {
            return penaltyRuleText;
        }

        public void setPenaltyRuleText(String penaltyRuleText) {
            this.penaltyRuleText = penaltyRuleText;
        }
    }

    public static class CircleTaskSummary {

        private Long totalCount;

        private Long completedCount;

        private Long remainingCount;

        private Long pendingPenaltyCount;

        private String penaltyRuleText;

        private Boolean readOnly;

        private String readOnlyReason;

        public Long getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(Long totalCount) {
            this.totalCount = totalCount;
        }

        public Long getCompletedCount() {
            return completedCount;
        }

        public void setCompletedCount(Long completedCount) {
            this.completedCount = completedCount;
        }

        public Long getRemainingCount() {
            return remainingCount;
        }

        public void setRemainingCount(Long remainingCount) {
            this.remainingCount = remainingCount;
        }

        public Long getPendingPenaltyCount() {
            return pendingPenaltyCount;
        }

        public void setPendingPenaltyCount(Long pendingPenaltyCount) {
            this.pendingPenaltyCount = pendingPenaltyCount;
        }

        public String getPenaltyRuleText() {
            return penaltyRuleText;
        }

        public void setPenaltyRuleText(String penaltyRuleText) {
            this.penaltyRuleText = penaltyRuleText;
        }

        public Boolean getReadOnly() {
            return readOnly;
        }

        public void setReadOnly(Boolean readOnly) {
            this.readOnly = readOnly;
        }

        public String getReadOnlyReason() {
            return readOnlyReason;
        }

        public void setReadOnlyReason(String readOnlyReason) {
            this.readOnlyReason = readOnlyReason;
        }
    }

    public static class CircleTaskOverview {

        private List<CircleTaskItem> tasks;

        private CircleTaskSummary summary;

        public List<CircleTaskItem> getTasks() {
            return tasks;
        }

        public void setTasks(List<CircleTaskItem> tasks) {
            this.tasks = tasks;
        }

        public CircleTaskSummary getSummary() {
            return summary;
        }

        public void setSummary(CircleTaskSummary summary) {
            this.summary = summary;
        }
    }

    public static class CircleRankingItem {

        private Long rankNum;

        private Long userId;

        private String nickname;

        private String avatar;

        private Long completedTaskCount;

        private Long circleCheckins;

        private Long streakDays;

        private String joinedAt;

        private BigDecimal score;

        public Long getRankNum() {
            return rankNum;
        }

        public void setRankNum(Long rankNum) {
            this.rankNum = rankNum;
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

        public Long getStreakDays() {
            return streakDays;
        }

        public void setStreakDays(Long streakDays) {
            this.streakDays = streakDays;
        }

        public String getJoinedAt() {
            return joinedAt;
        }

        public void setJoinedAt(String joinedAt) {
            this.joinedAt = joinedAt;
        }

        public BigDecimal getScore() {
            return score;
        }

        public void setScore(BigDecimal score) {
            this.score = score;
        }
    }

    public static class CircleRankingSummary {

        private String periodLabel;

        private String ruleText;

        private CircleRankingItem leader;

        private String updatedAt;

        public String getPeriodLabel() {
            return periodLabel;
        }

        public void setPeriodLabel(String periodLabel) {
            this.periodLabel = periodLabel;
        }

        public String getRuleText() {
            return ruleText;
        }

        public void setRuleText(String ruleText) {
            this.ruleText = ruleText;
        }

        public CircleRankingItem getLeader() {
            return leader;
        }

        public void setLeader(CircleRankingItem leader) {
            this.leader = leader;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }
    }

    public static class CircleRuleSummary {

        private String visibilityText;

        private String joinRuleText;

        private String durationText;

        private String penaltyRuleText;

        private String startDate;

        private String endDate;

        public String getVisibilityText() {
            return visibilityText;
        }

        public void setVisibilityText(String visibilityText) {
            this.visibilityText = visibilityText;
        }

        public String getJoinRuleText() {
            return joinRuleText;
        }

        public void setJoinRuleText(String joinRuleText) {
            this.joinRuleText = joinRuleText;
        }

        public String getDurationText() {
            return durationText;
        }

        public void setDurationText(String durationText) {
            this.durationText = durationText;
        }

        public String getPenaltyRuleText() {
            return penaltyRuleText;
        }

        public void setPenaltyRuleText(String penaltyRuleText) {
            this.penaltyRuleText = penaltyRuleText;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }
    }

    public static class CircleDepositSummary {

        private Boolean enabled;

        private BigDecimal amount;

        private String currentUserStatus;

        private String currentUserStatusLabel;

        private Long pendingCount;

        private Long paidCount;

        private Long refundedCount;

        private Long deductedCount;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getCurrentUserStatus() {
            return currentUserStatus;
        }

        public void setCurrentUserStatus(String currentUserStatus) {
            this.currentUserStatus = currentUserStatus;
        }

        public String getCurrentUserStatusLabel() {
            return currentUserStatusLabel;
        }

        public void setCurrentUserStatusLabel(String currentUserStatusLabel) {
            this.currentUserStatusLabel = currentUserStatusLabel;
        }

        public Long getPendingCount() {
            return pendingCount;
        }

        public void setPendingCount(Long pendingCount) {
            this.pendingCount = pendingCount;
        }

        public Long getPaidCount() {
            return paidCount;
        }

        public void setPaidCount(Long paidCount) {
            this.paidCount = paidCount;
        }

        public Long getRefundedCount() {
            return refundedCount;
        }

        public void setRefundedCount(Long refundedCount) {
            this.refundedCount = refundedCount;
        }

        public Long getDeductedCount() {
            return deductedCount;
        }

        public void setDeductedCount(Long deductedCount) {
            this.deductedCount = deductedCount;
        }
    }

    public static class CircleMemberItem {

        private Long membershipId;

        private Long userId;

        private String nickname;

        private String avatar;

        private String role;

        private String joinedAt;

        private Long streakDays;

        private Long totalCheckins;

        private BigDecimal weightChange;

        private String depositStatus;

        private Boolean removable;

        public Long getMembershipId() {
            return membershipId;
        }

        public void setMembershipId(Long membershipId) {
            this.membershipId = membershipId;
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

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getJoinedAt() {
            return joinedAt;
        }

        public void setJoinedAt(String joinedAt) {
            this.joinedAt = joinedAt;
        }

        public Long getStreakDays() {
            return streakDays;
        }

        public void setStreakDays(Long streakDays) {
            this.streakDays = streakDays;
        }

        public Long getTotalCheckins() {
            return totalCheckins;
        }

        public void setTotalCheckins(Long totalCheckins) {
            this.totalCheckins = totalCheckins;
        }

        public BigDecimal getWeightChange() {
            return weightChange;
        }

        public void setWeightChange(BigDecimal weightChange) {
            this.weightChange = weightChange;
        }

        public String getDepositStatus() {
            return depositStatus;
        }

        public void setDepositStatus(String depositStatus) {
            this.depositStatus = depositStatus;
        }

        public Boolean getRemovable() {
            return removable;
        }

        public void setRemovable(Boolean removable) {
            this.removable = removable;
        }
    }

    public static class CircleDetail {

        private CircleCard circle;

        private Boolean joined;

        private String memberRole;

        private List<CirclePreviewMember> previewMembers;

        private List<CircleFeedCard> recentFeeds;

        private Long pendingJoinRequestCount;

        private List<CircleGoalItem> goalList;

        private List<CircleRankingPreviewItem> rankingPreview;

        private CircleRuleSummary ruleSummary;

        private CircleDepositSummary depositSummary;

        private List<CircleTaskItem> todayTasks;

        private CircleTaskSummary taskSummary;

        private CircleRankingSummary rankingSummary;

        public CircleCard getCircle() {
            return circle;
        }

        public void setCircle(CircleCard circle) {
            this.circle = circle;
        }

        public Boolean getJoined() {
            return joined;
        }

        public void setJoined(Boolean joined) {
            this.joined = joined;
        }

        public String getMemberRole() {
            return memberRole;
        }

        public void setMemberRole(String memberRole) {
            this.memberRole = memberRole;
        }

        public List<CirclePreviewMember> getPreviewMembers() {
            return previewMembers;
        }

        public void setPreviewMembers(List<CirclePreviewMember> previewMembers) {
            this.previewMembers = previewMembers;
        }

        public List<CircleFeedCard> getRecentFeeds() {
            return recentFeeds;
        }

        public void setRecentFeeds(List<CircleFeedCard> recentFeeds) {
            this.recentFeeds = recentFeeds;
        }

        public Long getPendingJoinRequestCount() {
            return pendingJoinRequestCount;
        }

        public void setPendingJoinRequestCount(Long pendingJoinRequestCount) {
            this.pendingJoinRequestCount = pendingJoinRequestCount;
        }

        public List<CircleGoalItem> getGoalList() {
            return goalList;
        }

        public void setGoalList(List<CircleGoalItem> goalList) {
            this.goalList = goalList;
        }

        public List<CircleRankingPreviewItem> getRankingPreview() {
            return rankingPreview;
        }

        public void setRankingPreview(List<CircleRankingPreviewItem> rankingPreview) {
            this.rankingPreview = rankingPreview;
        }

        public CircleRuleSummary getRuleSummary() {
            return ruleSummary;
        }

        public void setRuleSummary(CircleRuleSummary ruleSummary) {
            this.ruleSummary = ruleSummary;
        }

        public CircleDepositSummary getDepositSummary() {
            return depositSummary;
        }

        public void setDepositSummary(CircleDepositSummary depositSummary) {
            this.depositSummary = depositSummary;
        }

        public List<CircleTaskItem> getTodayTasks() {
            return todayTasks;
        }

        public void setTodayTasks(List<CircleTaskItem> todayTasks) {
            this.todayTasks = todayTasks;
        }

        public CircleTaskSummary getTaskSummary() {
            return taskSummary;
        }

        public void setTaskSummary(CircleTaskSummary taskSummary) {
            this.taskSummary = taskSummary;
        }

        public CircleRankingSummary getRankingSummary() {
            return rankingSummary;
        }

        public void setRankingSummary(CircleRankingSummary rankingSummary) {
            this.rankingSummary = rankingSummary;
        }
    }

    public static class CircleMembershipState {

        private Long circleId;

        private Boolean joined;

        private String memberRole;

        private Long memberCount;

        private Boolean pendingApproval;

        public Long getCircleId() {
            return circleId;
        }

        public void setCircleId(Long circleId) {
            this.circleId = circleId;
        }

        public Boolean getJoined() {
            return joined;
        }

        public void setJoined(Boolean joined) {
            this.joined = joined;
        }

        public String getMemberRole() {
            return memberRole;
        }

        public void setMemberRole(String memberRole) {
            this.memberRole = memberRole;
        }

        public Long getMemberCount() {
            return memberCount;
        }

        public void setMemberCount(Long memberCount) {
            this.memberCount = memberCount;
        }

        public Boolean getPendingApproval() {
            return pendingApproval;
        }

        public void setPendingApproval(Boolean pendingApproval) {
            this.pendingApproval = pendingApproval;
        }
    }

    public static class CircleInvitePreview {

        private String token;

        private Long circleId;

        private String circleName;

        private String circleType;

        private Boolean approvalRequired;

        private Long expiresAt;

        private String membershipStatus;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public Long getCircleId() {
            return circleId;
        }

        public void setCircleId(Long circleId) {
            this.circleId = circleId;
        }

        public String getCircleName() {
            return circleName;
        }

        public void setCircleName(String circleName) {
            this.circleName = circleName;
        }

        public String getCircleType() {
            return circleType;
        }

        public void setCircleType(String circleType) {
            this.circleType = circleType;
        }

        public Boolean getApprovalRequired() {
            return approvalRequired;
        }

        public void setApprovalRequired(Boolean approvalRequired) {
            this.approvalRequired = approvalRequired;
        }

        public Long getExpiresAt() {
            return expiresAt;
        }

        public void setExpiresAt(Long expiresAt) {
            this.expiresAt = expiresAt;
        }

        public String getMembershipStatus() {
            return membershipStatus;
        }

        public void setMembershipStatus(String membershipStatus) {
            this.membershipStatus = membershipStatus;
        }
    }

    public static class CircleJoinRequestCard {

        private Long id;

        private Long userId;

        private String nickname;

        private String avatar;

        private String requestedAt;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
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

        public String getRequestedAt() {
            return requestedAt;
        }

        public void setRequestedAt(String requestedAt) {
            this.requestedAt = requestedAt;
        }
    }
}
