package com.ruoyi.weight.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 管理端系统通知发送请求。
 */
public class WeightNotificationSendRequest implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 接收范围：all 全部用户，selected 指定用户 */
    private String recipientMode;

    /** 指定接收用户ID列表 */
    private List<Long> userIds;

    /** 通知内容 */
    private String content;

    /** 预览内容 */
    private String preview;

    /** 目标类型 */
    private String targetType;

    /** 目标ID */
    private Long targetId;

    public String getRecipientMode()
    {
        return recipientMode;
    }

    public void setRecipientMode(String recipientMode)
    {
        this.recipientMode = recipientMode;
    }

    public List<Long> getUserIds()
    {
        return userIds;
    }

    public void setUserIds(List<Long> userIds)
    {
        this.userIds = userIds;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getPreview()
    {
        return preview;
    }

    public void setPreview(String preview)
    {
        this.preview = preview;
    }

    public String getTargetType()
    {
        return targetType;
    }

    public void setTargetType(String targetType)
    {
        this.targetType = targetType;
    }

    public Long getTargetId()
    {
        return targetId;
    }

    public void setTargetId(Long targetId)
    {
        this.targetId = targetId;
    }
}
