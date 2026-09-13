package com.ruoyi.api.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 移动端创建圈子请求 DTO
 */
public class ApiCircleCreateRequest {

    private String name;

    private String type;

    private Long categoryId;

    private String description;

    private String coverUrl;

    private Long durationDays;

    private BigDecimal depositRequired;

    private List<Long> selectedGoalTemplateIds;

    private List<CustomGoal> customGoals;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Long getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Long durationDays) {
        this.durationDays = durationDays;
    }

    public BigDecimal getDepositRequired() {
        return depositRequired;
    }

    public void setDepositRequired(BigDecimal depositRequired) {
        this.depositRequired = depositRequired;
    }

    public List<Long> getSelectedGoalTemplateIds() {
        return selectedGoalTemplateIds;
    }

    public void setSelectedGoalTemplateIds(List<Long> selectedGoalTemplateIds) {
        this.selectedGoalTemplateIds = selectedGoalTemplateIds;
    }

    public List<CustomGoal> getCustomGoals() {
        return customGoals;
    }

    public void setCustomGoals(List<CustomGoal> customGoals) {
        this.customGoals = customGoals;
    }

    public static class CustomGoal {

        private String metricCode;

        private String goalName;

        private BigDecimal targetValue;

        private String targetUnit;

        private String period;

        private String verificationType;

        private Boolean isRequired;

        public String getMetricCode() {
            return metricCode;
        }

        public void setMetricCode(String metricCode) {
            this.metricCode = metricCode;
        }

        public String getGoalName() {
            return goalName;
        }

        public void setGoalName(String goalName) {
            this.goalName = goalName;
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

        public String getVerificationType() {
            return verificationType;
        }

        public void setVerificationType(String verificationType) {
            this.verificationType = verificationType;
        }

        public Boolean getIsRequired() {
            return isRequired;
        }

        public void setIsRequired(Boolean isRequired) {
            this.isRequired = isRequired;
        }
    }
}
