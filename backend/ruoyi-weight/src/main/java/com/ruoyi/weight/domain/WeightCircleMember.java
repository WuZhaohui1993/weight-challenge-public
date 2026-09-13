package com.ruoyi.weight.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 圈子成员对象 weight_circle_member
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public class WeightCircleMember extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 圈子ID */
    @Excel(name = "圈子ID")
    private Long circleId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 角色（0管理员 1成员） */
    @Excel(name = "角色", readConverterExp = "0=管理员,1=成员")
    private String role;

    /** 加入时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "加入时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date joinedAt;

    /** 是否置顶 */
    @Excel(name = "是否置顶")
    private Integer isPinned;

    /** 连续打卡天数 */
    @Excel(name = "连续打卡天数")
    private Long streakDays;

    /** 押金状态（0未缴 1已缴 2已退 3已扣） */
    @Excel(name = "押金状态", readConverterExp = "0=未缴,1=已缴,2=已退,3=已扣")
    private String depositStatus;

    /** 累计打卡次数 */
    @Excel(name = "累计打卡次数")
    private Long totalCheckins;

    /** 体重变化量 */
    @Excel(name = "体重变化量")
    private BigDecimal weightChange;

    /** 状态（0正常 1退出） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=退出")
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

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setRole(String role) 
    {
        this.role = role;
    }

    public String getRole() 
    {
        return role;
    }

    public void setJoinedAt(Date joinedAt) 
    {
        this.joinedAt = joinedAt;
    }

    public Date getJoinedAt() 
    {
        return joinedAt;
    }

    public void setIsPinned(Integer isPinned) 
    {
        this.isPinned = isPinned;
    }

    public Integer getIsPinned() 
    {
        return isPinned;
    }

    public void setStreakDays(Long streakDays) 
    {
        this.streakDays = streakDays;
    }

    public Long getStreakDays() 
    {
        return streakDays;
    }

    public void setDepositStatus(String depositStatus) 
    {
        this.depositStatus = depositStatus;
    }

    public String getDepositStatus() 
    {
        return depositStatus;
    }

    public void setTotalCheckins(Long totalCheckins) 
    {
        this.totalCheckins = totalCheckins;
    }

    public Long getTotalCheckins() 
    {
        return totalCheckins;
    }

    public void setWeightChange(BigDecimal weightChange) 
    {
        this.weightChange = weightChange;
    }

    public BigDecimal getWeightChange() 
    {
        return weightChange;
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
            .append("userId", getUserId())
            .append("role", getRole())
            .append("joinedAt", getJoinedAt())
            .append("isPinned", getIsPinned())
            .append("streakDays", getStreakDays())
            .append("depositStatus", getDepositStatus())
            .append("totalCheckins", getTotalCheckins())
            .append("weightChange", getWeightChange())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
