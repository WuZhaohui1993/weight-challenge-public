package com.ruoyi.weight.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 评论对象 weight_comment
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public class WeightComment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 动态ID */
    @Excel(name = "动态ID")
    private Long feedId;

    /** 评论者用户ID */
    @Excel(name = "评论者用户ID")
    private Long userId;

    /** 评论内容 */
    @Excel(name = "评论内容")
    private String content;

    /** 评论图片 */
    private String images;

    /** 回复的评论ID */
    @Excel(name = "回复的评论ID")
    private Long replyToCommentId;

    /** 回复的用户ID */
    @Excel(name = "回复的用户ID")
    private Long replyToUserId;

    /** 点赞数 */
    @Excel(name = "点赞数")
    private Long likesCount;

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

    public void setFeedId(Long feedId) 
    {
        this.feedId = feedId;
    }

    public Long getFeedId() 
    {
        return feedId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
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

    public void setReplyToCommentId(Long replyToCommentId) 
    {
        this.replyToCommentId = replyToCommentId;
    }

    public Long getReplyToCommentId() 
    {
        return replyToCommentId;
    }

    public void setReplyToUserId(Long replyToUserId) 
    {
        this.replyToUserId = replyToUserId;
    }

    public Long getReplyToUserId() 
    {
        return replyToUserId;
    }

    public void setLikesCount(Long likesCount) 
    {
        this.likesCount = likesCount;
    }

    public Long getLikesCount() 
    {
        return likesCount;
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
            .append("feedId", getFeedId())
            .append("userId", getUserId())
            .append("content", getContent())
            .append("images", getImages())
            .append("replyToCommentId", getReplyToCommentId())
            .append("replyToUserId", getReplyToUserId())
            .append("likesCount", getLikesCount())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
