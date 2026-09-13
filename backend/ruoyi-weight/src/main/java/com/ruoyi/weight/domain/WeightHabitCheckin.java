package com.ruoyi.weight.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 习惯打卡记录对象 weight_habit_checkin
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public class WeightHabitCheckin extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 习惯ID */
    @Excel(name = "习惯ID")
    private Long habitId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 打卡时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "打卡时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date checkedAt;

    /** 备注 */
    @Excel(name = "备注")
    private String note;

    /** 打卡图片URL数组(JSON) */
    @Excel(name = "打卡图片URL数组(JSON)")
    private String images;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setHabitId(Long habitId) 
    {
        this.habitId = habitId;
    }

    public Long getHabitId() 
    {
        return habitId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setCheckedAt(Date checkedAt) 
    {
        this.checkedAt = checkedAt;
    }

    public Date getCheckedAt() 
    {
        return checkedAt;
    }

    public void setNote(String note) 
    {
        this.note = note;
    }

    public String getNote()
    {
        return note;
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
            .append("habitId", getHabitId())
            .append("userId", getUserId())
            .append("checkedAt", getCheckedAt())
            .append("note", getNote())
            .append("images", getImages())
            .append("createTime", getCreateTime())
            .toString();
    }
}
