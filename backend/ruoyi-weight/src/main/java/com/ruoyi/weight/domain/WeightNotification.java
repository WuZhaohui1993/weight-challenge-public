package com.ruoyi.weight.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 消息通知对象 weight_notification
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public class WeightNotification extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 接收通知的用户ID */
    @Excel(name = "接收通知的用户ID")
    private Long userId;

    /** 通知类型（like/comment/reply/follow/circle_join/circle_nudge/achievement/system） */
    @Excel(name = "通知类型", readConverterExp = "l=ike/comment/reply/follow/circle_join/circle_nudge/achievement/system")
    private String type;

    /** 触发通知的用户ID */
    @Excel(name = "触发通知的用户ID")
    private Long fromUserId;

    /** 目标类型（feed/comment/circle/achievement） */
    @Excel(name = "目标类型", readConverterExp = "f=eed/comment/circle/achievement")
    private String targetType;

    /** 目标ID */
    @Excel(name = "目标ID")
    private Long targetId;

    /** 通知内容 */
    @Excel(name = "通知内容")
    private String content;

    /** 预览内容 */
    @Excel(name = "预览内容")
    private String preview;

    /** 是否已读 */
    @Excel(name = "是否已读")
    private Integer isRead;

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

    public void setType(String type) 
    {
        this.type = type;
    }

    public String getType() 
    {
        return type;
    }

    public void setFromUserId(Long fromUserId) 
    {
        this.fromUserId = fromUserId;
    }

    public Long getFromUserId() 
    {
        return fromUserId;
    }

    public void setTargetType(String targetType) 
    {
        this.targetType = targetType;
    }

    public String getTargetType() 
    {
        return targetType;
    }

    public void setTargetId(Long targetId) 
    {
        this.targetId = targetId;
    }

    public Long getTargetId() 
    {
        return targetId;
    }

    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }

    public void setPreview(String preview) 
    {
        this.preview = preview;
    }

    public String getPreview() 
    {
        return preview;
    }

    public void setIsRead(Integer isRead) 
    {
        this.isRead = isRead;
    }

    public Integer getIsRead() 
    {
        return isRead;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("type", getType())
            .append("fromUserId", getFromUserId())
            .append("targetType", getTargetType())
            .append("targetId", getTargetId())
            .append("content", getContent())
            .append("preview", getPreview())
            .append("isRead", getIsRead())
            .append("createTime", getCreateTime())
            .toString();
    }
}
