package com.ruoyi.weight.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 圈子主对象 weight_circle
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public class WeightCircle extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 圈子名称 */
    @Excel(name = "圈子名称")
    private String name;

    /** 类型（0公开 1私密） */
    @Excel(name = "类型", readConverterExp = "0=公开,1=私密")
    private String type;

    /** 分类ID */
    @Excel(name = "分类ID")
    private Long categoryId;

    /** 圈子图标emoji */
    @Excel(name = "圈子图标emoji")
    private String icon;

    /** 封面图URL */
    @Excel(name = "封面图URL")
    private String coverUrl;

    /** 简介 */
    @Excel(name = "简介")
    private String description;

    /** 创建者用户ID */
    @Excel(name = "创建者用户ID")
    private Long creatorId;

    /** 成员数 */
    @Excel(name = "成员数")
    private Long memberCount;

    /** 押金金额 */
    @Excel(name = "押金金额")
    private BigDecimal depositRequired;

    /** 惩罚规则（0无 1提醒 2扣押金 3踢出） */
    @Excel(name = "惩罚规则", readConverterExp = "0=无,1=提醒,2=扣押金,3=踢出")
    private String penaltyRule;

    /** 持续天数（0永久） */
    @Excel(name = "持续天数", readConverterExp = "0=永久")
    private Long durationDays;

    /** 开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startDate;

    /** 结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endDate;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志（0存在 2删除） */
    private String delFlag;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    public void setType(String type) 
    {
        this.type = type;
    }

    public String getType() 
    {
        return type;
    }

    public void setCategoryId(Long categoryId) 
    {
        this.categoryId = categoryId;
    }

    public Long getCategoryId() 
    {
        return categoryId;
    }

    public void setIcon(String icon) 
    {
        this.icon = icon;
    }

    public String getIcon() 
    {
        return icon;
    }

    public void setCoverUrl(String coverUrl) 
    {
        this.coverUrl = coverUrl;
    }

    public String getCoverUrl() 
    {
        return coverUrl;
    }

    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }

    public void setCreatorId(Long creatorId) 
    {
        this.creatorId = creatorId;
    }

    public Long getCreatorId() 
    {
        return creatorId;
    }

    public void setMemberCount(Long memberCount) 
    {
        this.memberCount = memberCount;
    }

    public Long getMemberCount() 
    {
        return memberCount;
    }

    public void setDepositRequired(BigDecimal depositRequired) 
    {
        this.depositRequired = depositRequired;
    }

    public BigDecimal getDepositRequired() 
    {
        return depositRequired;
    }

    public void setPenaltyRule(String penaltyRule) 
    {
        this.penaltyRule = penaltyRule;
    }

    public String getPenaltyRule() 
    {
        return penaltyRule;
    }

    public void setDurationDays(Long durationDays) 
    {
        this.durationDays = durationDays;
    }

    public Long getDurationDays() 
    {
        return durationDays;
    }

    public void setStartDate(Date startDate) 
    {
        this.startDate = startDate;
    }

    public Date getStartDate() 
    {
        return startDate;
    }

    public void setEndDate(Date endDate) 
    {
        this.endDate = endDate;
    }

    public Date getEndDate() 
    {
        return endDate;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("type", getType())
            .append("categoryId", getCategoryId())
            .append("icon", getIcon())
            .append("coverUrl", getCoverUrl())
            .append("description", getDescription())
            .append("creatorId", getCreatorId())
            .append("memberCount", getMemberCount())
            .append("depositRequired", getDepositRequired())
            .append("penaltyRule", getPenaltyRule())
            .append("durationDays", getDurationDays())
            .append("startDate", getStartDate())
            .append("endDate", getEndDate())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
