package com.ruoyi.weight.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 关注动态流对象 weight_social_feed
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public class WeightSocialFeed extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 用户ID（接收者） */
    @Excel(name = "用户ID", readConverterExp = "接=收者")
    private Long userId;

    /** 动态ID */
    @Excel(name = "动态ID")
    private Long feedId;

    /** 动态发布者ID */
    @Excel(name = "动态发布者ID")
    private Long fromUserId;

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

    public void setFeedId(Long feedId) 
    {
        this.feedId = feedId;
    }

    public Long getFeedId() 
    {
        return feedId;
    }

    public void setFromUserId(Long fromUserId) 
    {
        this.fromUserId = fromUserId;
    }

    public Long getFromUserId() 
    {
        return fromUserId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("feedId", getFeedId())
            .append("fromUserId", getFromUserId())
            .append("createTime", getCreateTime())
            .toString();
    }
}
