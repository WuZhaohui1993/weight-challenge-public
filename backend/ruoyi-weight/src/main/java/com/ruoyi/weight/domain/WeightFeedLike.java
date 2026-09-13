package com.ruoyi.weight.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 动态点赞对象 weight_feed_like
 */
public class WeightFeedLike extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;

    @Excel(name = "动态ID")
    private Long feedId;

    @Excel(name = "用户ID")
    private Long userId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFeedId() {
        return feedId;
    }

    public void setFeedId(Long feedId) {
        this.feedId = feedId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("feedId", getFeedId())
            .append("userId", getUserId())
            .append("createTime", getCreateTime())
            .toString();
    }
}
