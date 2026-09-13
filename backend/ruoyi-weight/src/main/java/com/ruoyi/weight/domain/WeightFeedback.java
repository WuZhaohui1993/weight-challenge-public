package com.ruoyi.weight.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 用户反馈对象 weight_feedback
 *
 * @author ruoyi
 * @date 2026-04-26
 */
public class WeightFeedback extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 反馈类型 */
    @Excel(name = "反馈类型")
    private String category;

    /** 反馈内容 */
    @Excel(name = "反馈内容")
    private String content;

    /** 图片URL数组(JSON) */
    @Excel(name = "图片URL数组")
    private String images;

    /** 联系方式 */
    @Excel(name = "联系方式")
    private String contact;

    /** 来源页面 */
    @Excel(name = "来源页面")
    private String sourcePage;

    /** 环境信息(JSON) */
    @Excel(name = "环境信息")
    private String environmentJson;

    /** 处理状态 */
    @Excel(name = "处理状态")
    private String status;

    /** 优先级 */
    @Excel(name = "优先级")
    private String priority;

    /** 管理员回复 */
    @Excel(name = "管理员回复")
    private String replyContent;

    /** 回复人 */
    @Excel(name = "回复人")
    private String replyBy;

    /** 回复时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "回复时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date replyTime;

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

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getCategory()
    {
        return category;
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

    public void setContact(String contact)
    {
        this.contact = contact;
    }

    public String getContact()
    {
        return contact;
    }

    public void setSourcePage(String sourcePage)
    {
        this.sourcePage = sourcePage;
    }

    public String getSourcePage()
    {
        return sourcePage;
    }

    public void setEnvironmentJson(String environmentJson)
    {
        this.environmentJson = environmentJson;
    }

    public String getEnvironmentJson()
    {
        return environmentJson;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    public void setPriority(String priority)
    {
        this.priority = priority;
    }

    public String getPriority()
    {
        return priority;
    }

    public void setReplyContent(String replyContent)
    {
        this.replyContent = replyContent;
    }

    public String getReplyContent()
    {
        return replyContent;
    }

    public void setReplyBy(String replyBy)
    {
        this.replyBy = replyBy;
    }

    public String getReplyBy()
    {
        return replyBy;
    }

    public void setReplyTime(Date replyTime)
    {
        this.replyTime = replyTime;
    }

    public Date getReplyTime()
    {
        return replyTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("category", getCategory())
            .append("content", getContent())
            .append("images", getImages())
            .append("contact", getContact())
            .append("sourcePage", getSourcePage())
            .append("environmentJson", getEnvironmentJson())
            .append("status", getStatus())
            .append("priority", getPriority())
            .append("replyContent", getReplyContent())
            .append("replyBy", getReplyBy())
            .append("replyTime", getReplyTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
