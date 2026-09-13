package com.ruoyi.weight.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 同步偏好设置对象 weight_sync_preference
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public class WeightSyncPreference extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 圈子ID */
    @Excel(name = "圈子ID")
    private Long circleId;

    /** 是否自动同步 */
    @Excel(name = "是否自动同步")
    private Integer autoSync;

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

    public void setCircleId(Long circleId) 
    {
        this.circleId = circleId;
    }

    public Long getCircleId() 
    {
        return circleId;
    }

    public void setAutoSync(Integer autoSync) 
    {
        this.autoSync = autoSync;
    }

    public Integer getAutoSync() 
    {
        return autoSync;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("circleId", getCircleId())
            .append("autoSync", getAutoSync())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
