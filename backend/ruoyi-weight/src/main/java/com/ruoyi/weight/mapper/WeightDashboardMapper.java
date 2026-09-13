package com.ruoyi.weight.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 * 管理端仪表盘统计Mapper接口
 *
 * @author ruoyi
 * @date 2026-03-23
 */
public interface WeightDashboardMapper
{
    /**
     * 查询仪表盘概览统计
     *
     * @return 概览统计
     */
    public Map<String, Object> selectOverview();

    /**
     * 查询指定时间范围内的打卡趋势
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 打卡趋势
     */
    public List<Map<String, Object>> selectCheckinTrend(@Param("startTime") Date startTime,
            @Param("endTime") Date endTime);

    /**
     * 查询热门圈子
     *
     * @param limit 返回条数
     * @return 热门圈子
     */
    public List<Map<String, Object>> selectTopCircles(@Param("limit") int limit);

    /**
     * 查询内容运营统计
     *
     * @return 内容运营统计
     */
    public Map<String, Object> selectContentStats();
}
