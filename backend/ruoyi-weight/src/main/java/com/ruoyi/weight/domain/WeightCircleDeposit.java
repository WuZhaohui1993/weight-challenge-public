package com.ruoyi.weight.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 押金记录对象 weight_circle_deposit
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public class WeightCircleDeposit extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 圈子ID */
    @Excel(name = "圈子ID")
    private Long circleId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 押金金额 */
    @Excel(name = "押金金额")
    private BigDecimal amount;

    /** 状态（0待支付 1已支付 2已退还 3已扣除） */
    @Excel(name = "状态", readConverterExp = "0=待支付,1=已支付,2=已退还,3=已扣除")
    private String status;

    /** 支付时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "支付时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date paidAt;

    /** 退还时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "退还时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date refundedAt;

    /** 扣除原因 */
    @Excel(name = "扣除原因")
    private String deductReason;

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

    public void setAmount(BigDecimal amount) 
    {
        this.amount = amount;
    }

    public BigDecimal getAmount() 
    {
        return amount;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setPaidAt(Date paidAt) 
    {
        this.paidAt = paidAt;
    }

    public Date getPaidAt() 
    {
        return paidAt;
    }

    public void setRefundedAt(Date refundedAt) 
    {
        this.refundedAt = refundedAt;
    }

    public Date getRefundedAt() 
    {
        return refundedAt;
    }

    public void setDeductReason(String deductReason) 
    {
        this.deductReason = deductReason;
    }

    public String getDeductReason() 
    {
        return deductReason;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("circleId", getCircleId())
            .append("userId", getUserId())
            .append("amount", getAmount())
            .append("status", getStatus())
            .append("paidAt", getPaidAt())
            .append("refundedAt", getRefundedAt())
            .append("deductReason", getDeductReason())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
