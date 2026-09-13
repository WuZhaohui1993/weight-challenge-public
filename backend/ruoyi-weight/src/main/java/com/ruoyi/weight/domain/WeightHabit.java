package com.ruoyi.weight.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 习惯定义对象 weight_habit
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public class WeightHabit extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 习惯名称 */
    @Excel(name = "习惯名称")
    private String name;

    /** 图标emoji */
    @Excel(name = "图标emoji")
    private String icon;

    /** 时段（0早晨 1任意 2晚间） */
    @Excel(name = "时段", readConverterExp = "0=早晨,1=任意,2=晚间")
    private String period;

    /** 频率（0每日 1每周 2自定义） */
    @Excel(name = "频率", readConverterExp = "0=每日,1=每周,2=自定义")
    private String frequency;

    /** 提醒时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "提醒时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date reminderTime;

    /** 是否启用 */
    @Excel(name = "是否启用")
    private Integer isActive;

    /** 当前连续天数 */
    @Excel(name = "当前连续天数")
    private Long currentStreak;

    /** 最长连续天数 */
    @Excel(name = "最长连续天数")
    private Long longestStreak;

    /** 总打卡次数 */
    @Excel(name = "总打卡次数")
    private Long totalCheckins;

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

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    public void setIcon(String icon) 
    {
        this.icon = icon;
    }

    public String getIcon() 
    {
        return icon;
    }

    public void setPeriod(String period) 
    {
        this.period = period;
    }

    public String getPeriod() 
    {
        return period;
    }

    public void setFrequency(String frequency) 
    {
        this.frequency = frequency;
    }

    public String getFrequency() 
    {
        return frequency;
    }

    public void setReminderTime(Date reminderTime) 
    {
        this.reminderTime = reminderTime;
    }

    public Date getReminderTime() 
    {
        return reminderTime;
    }

    public void setIsActive(Integer isActive) 
    {
        this.isActive = isActive;
    }

    public Integer getIsActive() 
    {
        return isActive;
    }

    public void setCurrentStreak(Long currentStreak) 
    {
        this.currentStreak = currentStreak;
    }

    public Long getCurrentStreak() 
    {
        return currentStreak;
    }

    public void setLongestStreak(Long longestStreak) 
    {
        this.longestStreak = longestStreak;
    }

    public Long getLongestStreak() 
    {
        return longestStreak;
    }

    public void setTotalCheckins(Long totalCheckins) 
    {
        this.totalCheckins = totalCheckins;
    }

    public Long getTotalCheckins() 
    {
        return totalCheckins;
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
            .append("userId", getUserId())
            .append("name", getName())
            .append("icon", getIcon())
            .append("period", getPeriod())
            .append("frequency", getFrequency())
            .append("reminderTime", getReminderTime())
            .append("isActive", getIsActive())
            .append("currentStreak", getCurrentStreak())
            .append("longestStreak", getLongestStreak())
            .append("totalCheckins", getTotalCheckins())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
