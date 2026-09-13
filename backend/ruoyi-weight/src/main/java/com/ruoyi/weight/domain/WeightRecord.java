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

    /** BMI快照 */
    @Excel(name = "BMI快照")
    private BigDecimal bmi;

    /** 体脂率(%) */
    @Excel(name = "体脂率(%)")
    private BigDecimal bodyFatRate;

    /** 记录时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "记录时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date recordedAt;

    /** 同步到的圈子ID列表(JSON) */
    @Excel(name = "同步到的圈子ID列表(JSON)")
    private String syncToCircles;

    /** 记录图片URL数组(JSON) */
    @Excel(name = "记录图片URL数组(JSON)")
    private String images;

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

    public void setBmi(BigDecimal bmi)
    {
        this.bmi = bmi;
    }

    public BigDecimal getBmi()
    {
        return bmi;
    }

    public void setBodyFatRate(BigDecimal bodyFatRate)
    {
        this.bodyFatRate = bodyFatRate;
    }

    public BigDecimal getBodyFatRate()
    {
        return bodyFatRate;
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

    public void setImages(String images)
    {
        this.images = images;
    }

    public String getImages()
    {
        return images;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("weight", getWeight())
            .append("bmi", getBmi())
            .append("bodyFatRate", getBodyFatRate())
            .append("recordedAt", getRecordedAt())
            .append("syncToCircles", getSyncToCircles())
            .append("images", getImages())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
