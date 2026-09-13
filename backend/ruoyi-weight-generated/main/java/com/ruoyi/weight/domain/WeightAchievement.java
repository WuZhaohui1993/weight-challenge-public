package com.ruoyi.weight.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 成就徽章对象 weight_achievement
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public class WeightAchievement extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 徽章类型 */
    @Excel(name = "徽章类型")
    private String badgeType;

    /** 徽章名称 */
    @Excel(name = "徽章名称")
    private String badgeName;

    /** 徽章图标 */
    @Excel(name = "徽章图标")
    private String badgeIcon;

    /** 徽章描述 */
    @Excel(name = "徽章描述")
    private String badgeDescription;

    /** 解锁时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "解锁时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date unlockedAt;

    /** 进度(0-100) */
    @Excel(name = "进度(0-100)")
    private BigDecimal progress;

    /** 是否已解锁 */
    @Excel(name = "是否已解锁")
    private Integer isUnlocked;

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

    public void setBadgeType(String badgeType) 
    {
        this.badgeType = badgeType;
    }

    public String getBadgeType() 
    {
        return badgeType;
    }

    public void setBadgeName(String badgeName) 
    {
        this.badgeName = badgeName;
    }

    public String getBadgeName() 
    {
        return badgeName;
    }

    public void setBadgeIcon(String badgeIcon) 
    {
        this.badgeIcon = badgeIcon;
    }

    public String getBadgeIcon() 
    {
        return badgeIcon;
    }

    public void setBadgeDescription(String badgeDescription) 
    {
        this.badgeDescription = badgeDescription;
    }

    public String getBadgeDescription() 
    {
        return badgeDescription;
    }

    public void setUnlockedAt(Date unlockedAt) 
    {
        this.unlockedAt = unlockedAt;
    }

    public Date getUnlockedAt() 
    {
        return unlockedAt;
    }

    public void setProgress(BigDecimal progress) 
    {
        this.progress = progress;
    }

    public BigDecimal getProgress() 
    {
        return progress;
    }

    public void setIsUnlocked(Integer isUnlocked) 
    {
        this.isUnlocked = isUnlocked;
    }

    public Integer getIsUnlocked() 
    {
        return isUnlocked;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("badgeType", getBadgeType())
            .append("badgeName", getBadgeName())
            .append("badgeIcon", getBadgeIcon())
            .append("badgeDescription", getBadgeDescription())
            .append("unlockedAt", getUnlockedAt())
            .append("progress", getProgress())
            .append("isUnlocked", getIsUnlocked())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
