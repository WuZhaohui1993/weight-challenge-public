package com.ruoyi.weight.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 饮水记录对象 weight_water_record
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public class WeightWaterRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 杯数 */
    @Excel(name = "杯数")
    private Long cups;

    /** 毫升数 */
    @Excel(name = "毫升数")
    private Long ml;

    /** 记录时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "记录时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date recordedAt;

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

    public void setCups(Long cups) 
    {
        this.cups = cups;
    }

    public Long getCups() 
    {
        return cups;
    }

    public void setMl(Long ml) 
    {
        this.ml = ml;
    }

    public Long getMl() 
    {
        return ml;
    }

    public void setRecordedAt(Date recordedAt) 
    {
        this.recordedAt = recordedAt;
    }

    public Date getRecordedAt()
    {
        return recordedAt;
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
            .append("cups", getCups())
            .append("ml", getMl())
            .append("recordedAt", getRecordedAt())
            .append("images", getImages())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
