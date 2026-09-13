package com.ruoyi.weight.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 饮食记录对象 weight_food_record
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public class WeightFoodRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 餐类型（0早餐 1午餐 2晚餐 3加餐） */
    @Excel(name = "餐类型", readConverterExp = "0=早餐,1=午餐,2=晚餐,3=加餐")
    private String mealType;

    /** 食物名称 */
    @Excel(name = "食物名称")
    private String foodName;

    /** 卡路里 */
    @Excel(name = "卡路里")
    private Long calories;

    /** 蛋白质(g) */
    @Excel(name = "蛋白质(g)")
    private BigDecimal protein;

    /** 脂肪(g) */
    @Excel(name = "脂肪(g)")
    private BigDecimal fat;

    /** 碳水化合物(g) */
    @Excel(name = "碳水化合物(g)")
    private BigDecimal carbs;

    /** AI识别图片URL */
    @Excel(name = "AI识别图片URL")
    private String imageUrl;

    /** 记录图片URL数组(JSON) */
    @Excel(name = "记录图片URL数组(JSON)")
    private String images;

    /** 记录时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "记录时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
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

    public void setMealType(String mealType) 
    {
        this.mealType = mealType;
    }

    public String getMealType() 
    {
        return mealType;
    }

    public void setFoodName(String foodName) 
    {
        this.foodName = foodName;
    }

    public String getFoodName() 
    {
        return foodName;
    }

    public void setCalories(Long calories) 
    {
        this.calories = calories;
    }

    public Long getCalories() 
    {
        return calories;
    }

    public void setProtein(BigDecimal protein) 
    {
        this.protein = protein;
    }

    public BigDecimal getProtein() 
    {
        return protein;
    }

    public void setFat(BigDecimal fat) 
    {
        this.fat = fat;
    }

    public BigDecimal getFat() 
    {
        return fat;
    }

    public void setCarbs(BigDecimal carbs) 
    {
        this.carbs = carbs;
    }

    public BigDecimal getCarbs() 
    {
        return carbs;
    }

    public void setImageUrl(String imageUrl) 
    {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    public void setImages(String images)
    {
        this.images = images;
    }

    public String getImages()
    {
        return images;
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
            .append("mealType", getMealType())
            .append("foodName", getFoodName())
            .append("calories", getCalories())
            .append("protein", getProtein())
            .append("fat", getFat())
            .append("carbs", getCarbs())
            .append("imageUrl", getImageUrl())
            .append("images", getImages())
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
