package com.ruoyi.weight.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 排行榜缓存对象 weight_circle_ranking
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
public class WeightCircleRanking extends BaseEntity
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

    /** 周期（0日榜 1周榜 2月榜） */
    @Excel(name = "周期", readConverterExp = "0=日榜,1=周榜,2=月榜")
    private String period;

    /** 排名 */
    @Excel(name = "排名")
    private Long rankNum;

    /** 得分 */
    @Excel(name = "得分")
    private BigDecimal score;

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

    public void setPeriod(String period) 
    {
        this.period = period;
    }

    public String getPeriod() 
    {
        return period;
    }

    public void setRankNum(Long rankNum) 
    {
        this.rankNum = rankNum;
    }

    public Long getRankNum() 
    {
        return rankNum;
    }

    public void setScore(BigDecimal score) 
    {
        this.score = score;
    }

    public BigDecimal getScore() 
    {
        return score;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("circleId", getCircleId())
            .append("userId", getUserId())
            .append("period", getPeriod())
            .append("rankNum", getRankNum())
            .append("score", getScore())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
