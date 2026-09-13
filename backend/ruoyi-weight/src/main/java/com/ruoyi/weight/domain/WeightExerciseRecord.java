package com.ruoyi.weight.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 运动记录对象 weight_exercise_record
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public class WeightExerciseRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 运动类型 */
    @Excel(name = "运动类型")
    private String exerciseType;

    /** 时长(分钟) */
    @Excel(name = "时长(分钟)")
    private Long durationMinutes;

    /** 消耗卡路里 */
    @Excel(name = "消耗卡路里")
    private Long caloriesBurned;

    /** 距离(km) */
    @Excel(name = "距离(km)")
    private BigDecimal distance;

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

    public void setExerciseType(String exerciseType) 
    {
        this.exerciseType = exerciseType;
    }

    public String getExerciseType() 
    {
        return exerciseType;
    }

    public void setDurationMinutes(Long durationMinutes) 
    {
        this.durationMinutes = durationMinutes;
    }

    public Long getDurationMinutes() 
    {
        return durationMinutes;
    }

    public void setCaloriesBurned(Long caloriesBurned) 
    {
        this.caloriesBurned = caloriesBurned;
    }

    public Long getCaloriesBurned() 
    {
        return caloriesBurned;
    }

    public void setDistance(BigDecimal distance) 
    {
        this.distance = distance;
    }

    public BigDecimal getDistance() 
    {
        return distance;
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
            .append("exerciseType", getExerciseType())
            .append("durationMinutes", getDurationMinutes())
            .append("caloriesBurned", getCaloriesBurned())
            .append("distance", getDistance())
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
