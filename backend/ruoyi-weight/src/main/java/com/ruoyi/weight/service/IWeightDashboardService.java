package com.ruoyi.weight.service;

import java.util.List;
import java.util.Map;

/**
 * 管理端仪表盘统计Service接口
 *
 * @author ruoyi
 * @date 2026-03-23
 */
public interface IWeightDashboardService
{
    /**
     * 查询仪表盘概览统计
     *
     * @return 概览统计
     */
    public Map<String, Object> getOverview();

    /**
     * 查询最近7天打卡趋势
     *
     * @return 打卡趋势
     */
    public List<Map<String, Object>> getCheckinTrend();

    /**
     * 查询热门圈子Top5
     *
     * @return 热门圈子
     */
    public List<Map<String, Object>> getTopCircles();

    /**
     * 查询内容运营统计
     *
     * @return 内容运营统计
     */
    public Map<String, Object> getContentStats();
}
