package com.ruoyi.weight.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 体重记录对象 weight_record
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public class WeightRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 体重(kg) */
    @Excel(name = "体重(kg)")
    private BigDecimal weight;

    /** 记录时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "记录时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date recordedAt;

    /** 同步到的圈子ID列表(JSON) */
    @Excel(name = "同步到的圈子ID列表(JSON)")
    private String syncToCircles;

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

    public void setWeight(BigDecimal weight) 
    {
        this.weight = weight;
    }

    public BigDecimal getWeight() 
    {
        return weight;
    }

    public void setRecordedAt(Date recordedAt) 
    {
        this.recordedAt = recordedAt;
    }

    public Date getRecordedAt() 
    {
        return recordedAt;
    }

    public void setSyncToCircles(String syncToCircles) 
    {
        this.syncToCircles = syncToCircles;
    }

    public String getSyncToCircles() 
    {
        return syncToCircles;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("weight", getWeight())
            .append("recordedAt", getRecordedAt())
            .append("syncToCircles", getSyncToCircles())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
