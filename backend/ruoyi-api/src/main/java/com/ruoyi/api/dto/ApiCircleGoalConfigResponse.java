package com.ruoyi.api.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 圈子目标配置目录响应
 */
public class ApiCircleGoalConfigResponse {

    private List<GoalTemplateItem> templates;

    private List<MetricOptionItem> metricOptions;

    private List<OptionItem> periodOptions;

    private List<OptionItem> verificationOptions;

    public List<GoalTemplateItem> getTemplates() {
        return templates;
    }

    public void setTemplates(List<GoalTemplateItem> templates) {
        this.templates = templates;
    }

    public List<MetricOptionItem> getMetricOptions() {
        return metricOptions;
    }

    public void setMetricOptions(List<MetricOptionItem> metricOptions) {
        this.metricOptions = metricOptions;
    }

    public List<OptionItem> getPeriodOptions() {
        return periodOptions;
    }

    public void setPeriodOptions(List<OptionItem> periodOptions) {
        this.periodOptions = periodOptions;
    }

    public List<OptionItem> getVerificationOptions() {
        return verificationOptions;
    }

    public void setVerificationOptions(List<OptionItem> verificationOptions) {
        this.verificationOptions = verificationOptions;
    }

    public static class GoalTemplateItem {

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

        public Boolean getRequired() {
            return required;
        }

        public void setRequired(Boolean required) {
            this.required = required;
        }
    }

    public static class MetricOptionItem {

        private String value;

        private String label;

        private String goalType;

        private String defaultUnit;

        private String description;

        private List<String> verificationTypes;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getGoalType() {
            return goalType;
        }

        public void setGoalType(String goalType) {
            this.goalType = goalType;
        }

        public String getDefaultUnit() {
            return defaultUnit;
        }

        public void setDefaultUnit(String defaultUnit) {
            this.defaultUnit = defaultUnit;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<String> getVerificationTypes() {
            return verificationTypes;
        }

        public void setVerificationTypes(List<String> verificationTypes) {
            this.verificationTypes = verificationTypes;
        }
    }

    public static class OptionItem {

        private String value;

        private String label;

        public OptionItem() {
        }

        public OptionItem(String value, String label) {
            this.value = value;
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }
}
