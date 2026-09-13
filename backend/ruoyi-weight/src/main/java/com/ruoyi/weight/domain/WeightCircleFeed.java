package com.ruoyi.weight.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 圈子动态对象 weight_circle_feed
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public class WeightCircleFeed extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 圈子ID */
    @Excel(name = "圈子ID")
    private Long circleId;

    /** 发布者用户ID */
    @Excel(name = "发布者用户ID")
    private Long userId;

    /** 动态类型（weight/food/exercise/habit/text） */
    @Excel(name = "动态类型", readConverterExp = "w=eight/food/exercise/habit/text")
    private String feedType;

    /** 可见范围（public/circle） */
    @Excel(name = "可见范围")
    private String visibilityScope;

    /** 来源圈子ID */
    @Excel(name = "来源圈子ID")
    private Long originCircleId;

    /** 动态文本内容 */
    @Excel(name = "动态文本内容")
    private String content;

    /** 图片URL数组(JSON) */
    @Excel(name = "图片URL数组(JSON)")
    private String images;

    /** 关联的原始记录ID */
    @Excel(name = "关联的原始记录ID")
    private Long sourceRecordId;

    /** 关联记录类型 */
    @Excel(name = "关联记录类型")
    private String sourceRecordType;

    /** 点赞数 */
    @Excel(name = "点赞数")
    private Long likesCount;

    /** 评论数 */
    @Excel(name = "评论数")
    private Long commentsCount;

    /** 是否精选 */
    @Excel(name = "是否精选")
    private Integer isFeatured;

    /** 状态（0正常 1待审核 2已屏蔽） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=待审核,2=已屏蔽")
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

    public void setFeedType(String feedType) 
    {
        this.feedType = feedType;
    }

    public String getFeedType() 
    {
        return feedType;
    }

    public void setVisibilityScope(String visibilityScope)
    {
        this.visibilityScope = visibilityScope;
    }

    public String getVisibilityScope()
    {
        return visibilityScope;
    }

    public void setOriginCircleId(Long originCircleId)
    {
        this.originCircleId = originCircleId;
    }

    public Long getOriginCircleId()
    {
        return originCircleId;
    }

    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }

    public void setImages(String images) 
    {
        this.images = images;
    }

    public String getImages() 
    {
        return images;
    }

    public void setSourceRecordId(Long sourceRecordId) 
    {
        this.sourceRecordId = sourceRecordId;
    }

    public Long getSourceRecordId() 
    {
        return sourceRecordId;
    }

    public void setSourceRecordType(String sourceRecordType) 
    {
        this.sourceRecordType = sourceRecordType;
    }

    public String getSourceRecordType() 
    {
        return sourceRecordType;
    }

    public void setLikesCount(Long likesCount) 
    {
        this.likesCount = likesCount;
    }

    public Long getLikesCount() 
    {
        return likesCount;
    }

    public void setCommentsCount(Long commentsCount) 
    {
        this.commentsCount = commentsCount;
    }

    public Long getCommentsCount() 
    {
        return commentsCount;
    }

    public void setIsFeatured(Integer isFeatured) 
    {
        this.isFeatured = isFeatured;
    }

    public Integer getIsFeatured() 
    {
        return isFeatured;
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
            .append("circleId", getCircleId())
            .append("userId", getUserId())
            .append("feedType", getFeedType())
            .append("visibilityScope", getVisibilityScope())
            .append("originCircleId", getOriginCircleId())
            .append("content", getContent())
            .append("images", getImages())
            .append("sourceRecordId", getSourceRecordId())
            .append("sourceRecordType", getSourceRecordType())
            .append("likesCount", getLikesCount())
            .append("commentsCount", getCommentsCount())
            .append("isFeatured", getIsFeatured())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
