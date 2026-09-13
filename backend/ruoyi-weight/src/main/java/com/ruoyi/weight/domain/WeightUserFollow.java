package com.ruoyi.weight.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用户关注关系对象 weight_user_follow
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public class WeightUserFollow extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 关注者用户ID */
    @Excel(name = "关注者用户ID")
    private Long followerId;

    /** 被关注者用户ID */
    @Excel(name = "被关注者用户ID")
    private Long followingId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setFollowerId(Long followerId) 
    {
        this.followerId = followerId;
    }

    public Long getFollowerId() 
    {
        return followerId;
    }

    public void setFollowingId(Long followingId) 
    {
        this.followingId = followingId;
    }

    public Long getFollowingId() 
    {
        return followingId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("followerId", getFollowerId())
            .append("followingId", getFollowingId())
            .append("createTime", getCreateTime())
            .toString();
    }
}
