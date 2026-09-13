package com.ruoyi.weight.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 动态同步圈子关系对象 weight_feed_circle_sync
 *
 * @author ruoyi
 * @date 2026-03-24
 */
public class WeightFeedCircleSync extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 动态ID */
    @Excel(name = "动态ID")
    private Long feedId;

    /** 圈子ID */
    @Excel(name = "圈子ID")
    private Long circleId;

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

    public void setCircleId(Long circleId)
    {
        this.circleId = circleId;
    }

    public Long getCircleId()
    {
        return circleId;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("feedId", getFeedId())
                .append("circleId", getCircleId())
                .append("createTime", getCreateTime())
                .toString();
    }
}
