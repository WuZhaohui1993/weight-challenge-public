package com.ruoyi.weight.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 每日任务对象 weight_daily_task
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public class WeightDailyTask extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 任务日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "任务日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date taskDate;

    /** 任务类型（circle/habit/system） */
    @Excel(name = "任务类型", readConverterExp = "c=ircle/habit/system")
    private String taskType;

    /** 任务名称 */
    @Excel(name = "任务名称")
    private String taskName;

    /** 来源ID（圈子目标ID/习惯ID/系统任务ID） */
    @Excel(name = "来源ID", readConverterExp = "圈=子目标ID/习惯ID/系统任务ID")
    private Long sourceId;

    /** 目标值 */
    @Excel(name = "目标值")
    private BigDecimal targetValue;

    /** 当前值 */
    @Excel(name = "当前值")
    private BigDecimal currentValue;

    /** 是否完成 */
    @Excel(name = "是否完成")
    private Integer isCompleted;

    /** 完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "完成时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date completedAt;

    /** 积分奖励 */
    @Excel(name = "积分奖励")
    private Long pointsReward;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setTaskDate(Date taskDate) 
    {
        this.taskDate = taskDate;
    }

    public Date getTaskDate() 
    {
        return taskDate;
    }

    public void setTaskType(String taskType) 
    {
        this.taskType = taskType;
    }

    public String getTaskType() 
    {
        return taskType;
    }

    public void setTaskName(String taskName) 
    {
        this.taskName = taskName;
    }

    public String getTaskName() 
    {
        return taskName;
    }

    public void setSourceId(Long sourceId) 
    {
        this.sourceId = sourceId;
    }

    public Long getSourceId() 
    {
        return sourceId;
    }

    public void setTargetValue(BigDecimal targetValue) 
    {
        this.targetValue = targetValue;
    }

    public BigDecimal getTargetValue() 
    {
        return targetValue;
    }

    public void setCurrentValue(BigDecimal currentValue) 
    {
        this.currentValue = currentValue;
    }

    public BigDecimal getCurrentValue() 
    {
        return currentValue;
    }

    public void setIsCompleted(Integer isCompleted) 
    {
        this.isCompleted = isCompleted;
    }

    public Integer getIsCompleted() 
    {
        return isCompleted;
    }

    public void setCompletedAt(Date completedAt) 
    {
        this.completedAt = completedAt;
    }

    public Date getCompletedAt() 
    {
        return completedAt;
    }

    public void setPointsReward(Long pointsReward) 
    {
        this.pointsReward = pointsReward;
    }

    public Long getPointsReward() 
    {
        return pointsReward;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("taskDate", getTaskDate())
            .append("taskType", getTaskType())
            .append("taskName", getTaskName())
            .append("sourceId", getSourceId())
            .append("targetValue", getTargetValue())
            .append("currentValue", getCurrentValue())
            .append("isCompleted", getIsCompleted())
            .append("completedAt", getCompletedAt())
            .append("pointsReward", getPointsReward())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
