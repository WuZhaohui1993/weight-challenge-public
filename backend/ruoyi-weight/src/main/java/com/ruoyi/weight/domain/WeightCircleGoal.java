package com.ruoyi.weight.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 圈子目标对象 weight_circle_goal
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public class WeightCircleGoal extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 圈子ID */
    @Excel(name = "圈子ID")
    private Long circleId;

    /** 目标类型（weight_loss/checkin/exercise/diet/habit/custom） */
    @Excel(name = "目标类型", readConverterExp = "w=eight_loss/checkin/exercise/diet/habit/custom")
    private String goalType;

    /** 目标名称 */
    @Excel(name = "目标名称")
    private String goalName;

    /** 目标描述 */
    @Excel(name = "目标描述")
    private String description;

    /** 指标编码 */
    @Excel(name = "指标编码")
    private String metricCode;

    /** 目标值 */
    @Excel(name = "目标值")
    private BigDecimal targetValue;

    /** 目标单位 */
    @Excel(name = "目标单位")
    private String targetUnit;

    /** 周期（0每日 1每周 2每月 3总计） */
    @Excel(name = "周期", readConverterExp = "0=每日,1=每周,2=每月,3=总计")
    private String period;

    /** 验证方式（0自动 1手动 2拍照 3审核） */
    @Excel(name = "验证方式", readConverterExp = "0=自动,1=手动,2=拍照,3=审核")
    private String verificationType;

    /** 未完成惩罚描述 */
    @Excel(name = "未完成惩罚描述")
    private String penaltyForFailure;

    /** 是否必选 */
    @Excel(name = "是否必选")
    private Integer isRequired;

    /** 排序 */
    @Excel(name = "排序")
    private Long sortOrder;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setCircleId(Long circleId) 
    {
        this.circleId = circleId;
    }

    public Long getCircleId() 
    {
        return circleId;
    }

    public void setGoalType(String goalType) 
    {
        this.goalType = goalType;
    }

    public String getGoalType() 
    {
        return goalType;
    }

    public void setGoalName(String goalName) 
    {
        this.goalName = goalName;
    }

    public String getGoalName() 
    {
        return goalName;
    }

    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }

    public void setMetricCode(String metricCode)
    {
        this.metricCode = metricCode;
    }

    public String getMetricCode()
    {
        return metricCode;
    }

    public void setTargetValue(BigDecimal targetValue) 
    {
        this.targetValue = targetValue;
    }

    public BigDecimal getTargetValue() 
    {
        return targetValue;
    }

    public void setTargetUnit(String targetUnit) 
    {
        this.targetUnit = targetUnit;
    }

    public String getTargetUnit() 
    {
        return targetUnit;
    }

    public void setPeriod(String period) 
    {
        this.period = period;
    }

    public String getPeriod() 
    {
        return period;
    }

    public void setVerificationType(String verificationType) 
    {
        this.verificationType = verificationType;
    }

    public String getVerificationType() 
    {
        return verificationType;
    }

    public void setPenaltyForFailure(String penaltyForFailure) 
    {
        this.penaltyForFailure = penaltyForFailure;
    }

    public String getPenaltyForFailure() 
    {
        return penaltyForFailure;
    }

    public void setIsRequired(Integer isRequired) 
    {
        this.isRequired = isRequired;
    }

    public Integer getIsRequired() 
    {
        return isRequired;
    }

    public void setSortOrder(Long sortOrder) 
    {
        this.sortOrder = sortOrder;
    }

    public Long getSortOrder() 
    {
        return sortOrder;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("circleId", getCircleId())
            .append("goalType", getGoalType())
            .append("goalName", getGoalName())
            .append("description", getDescription())
            .append("metricCode", getMetricCode())
            .append("targetValue", getTargetValue())
            .append("targetUnit", getTargetUnit())
            .append("period", getPeriod())
            .append("verificationType", getVerificationType())
            .append("penaltyForFailure", getPenaltyForFailure())
            .append("isRequired", getIsRequired())
            .append("sortOrder", getSortOrder())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
