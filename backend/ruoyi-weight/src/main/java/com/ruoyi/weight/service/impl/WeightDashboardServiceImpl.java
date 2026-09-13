package com.ruoyi.weight.service.impl;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weight.mapper.WeightDashboardMapper;
import com.ruoyi.weight.service.IWeightDashboardService;

/**
 * 管理端仪表盘统计Service业务层处理
 *
 * @author ruoyi
 * @date 2026-03-23
 */
@Service
public class WeightDashboardServiceImpl implements IWeightDashboardService
{
    private static final int TREND_DAYS = 7;

    private static final int TOP_CIRCLE_LIMIT = 5;

    private static final String DEFAULT_CIRCLE_ICON = "⚖️";

    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER = DateTimeFormatter.ofPattern("MM-dd");

    @Autowired
    private WeightDashboardMapper weightDashboardMapper;

    /**
     * 查询仪表盘概览统计
     *
     * @return 概览统计
     */
    @Override
    public Map<String, Object> getOverview()
    {
        Map<String, Object> rawOverview = weightDashboardMapper.selectOverview();
        Map<String, Object> overview = new LinkedHashMap<>();
        overview.put("totalUsers", getLongValue(rawOverview, "totalUsers"));
        overview.put("totalCircles", getLongValue(rawOverview, "totalCircles"));
        overview.put("todayCheckins", getLongValue(rawOverview, "todayCheckins"));
        overview.put("totalRecords", getLongValue(rawOverview, "totalRecords"));
        overview.put("weightRecords", getLongValue(rawOverview, "weightRecords"));
        overview.put("foodRecords", getLongValue(rawOverview, "foodRecords"));
        overview.put("exerciseRecords", getLongValue(rawOverview, "exerciseRecords"));
        overview.put("waterRecords", getLongValue(rawOverview, "waterRecords"));
        overview.put("habitCheckins", getLongValue(rawOverview, "habitCheckins"));
        overview.put("todayFeeds", getLongValue(rawOverview, "todayFeeds"));
        overview.put("pendingFeedback", getLongValue(rawOverview, "pendingFeedback"));
        overview.put("pendingContent", getLongValue(rawOverview, "pendingContent"));
        overview.put("blockedContent", getLongValue(rawOverview, "blockedContent"));
        overview.put("unreadNotifications", getLongValue(rawOverview, "unreadNotifications"));
        return overview;
    }

    /**
     * 查询最近7天打卡趋势
     *
     * @return 打卡趋势
     */
    @Override
    public List<Map<String, Object>> getCheckinTrend()
    {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(TREND_DAYS - 1L);
        List<Map<String, Object>> rawTrend = weightDashboardMapper.selectCheckinTrend(
                Timestamp.valueOf(startDate.atStartOfDay()),
                Timestamp.valueOf(today.plusDays(1L).atStartOfDay()));

        Map<String, Long> countByDate = new HashMap<>();
        for (Map<String, Object> item : rawTrend)
        {
            Object statDate = item.get("statDate");
            if (statDate != null)
            {
                countByDate.put(String.valueOf(statDate), getLongValue(item, "count"));
            }
        }

        List<Map<String, Object>> trend = new ArrayList<>(TREND_DAYS);
        for (int i = 0; i < TREND_DAYS; i++)
        {
            LocalDate currentDate = startDate.plusDays(i);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("date", currentDate.format(DISPLAY_DATE_FORMATTER));
            item.put("count", countByDate.getOrDefault(currentDate.toString(), 0L));
            trend.add(item);
        }
        return trend;
    }

    /**
     * 查询热门圈子Top5
     *
     * @return 热门圈子
     */
    @Override
    public List<Map<String, Object>> getTopCircles()
    {
        List<Map<String, Object>> rawTopCircles = weightDashboardMapper.selectTopCircles(TOP_CIRCLE_LIMIT);
        List<Map<String, Object>> topCircles = new ArrayList<>(rawTopCircles.size());
        for (Map<String, Object> rawCircle : rawTopCircles)
        {
            Map<String, Object> circle = new LinkedHashMap<>();
            circle.put("id", rawCircle.get("id"));
            circle.put("name", rawCircle.get("name"));
            circle.put("icon", getStringValue(rawCircle.get("icon"), DEFAULT_CIRCLE_ICON));
            circle.put("memberCount", getLongValue(rawCircle, "memberCount"));
            topCircles.add(circle);
        }
        return topCircles;
    }

    /**
     * 查询内容运营统计
     *
     * @return 内容运营统计
     */
    @Override
    public Map<String, Object> getContentStats()
    {
        Map<String, Object> rawStats = weightDashboardMapper.selectContentStats();
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("todayFeeds", getLongValue(rawStats, "todayFeeds"));
        stats.put("todayComments", getLongValue(rawStats, "todayComments"));
        stats.put("publicFeeds", getLongValue(rawStats, "publicFeeds"));
        stats.put("circleFeeds", getLongValue(rawStats, "circleFeeds"));
        stats.put("pendingFeeds", getLongValue(rawStats, "pendingFeeds"));
        stats.put("blockedFeeds", getLongValue(rawStats, "blockedFeeds"));
        stats.put("pendingComments", getLongValue(rawStats, "pendingComments"));
        stats.put("blockedComments", getLongValue(rawStats, "blockedComments"));
        stats.put("pendingFeedback", getLongValue(rawStats, "pendingFeedback"));
        stats.put("processingFeedback", getLongValue(rawStats, "processingFeedback"));
        stats.put("resolvedFeedback", getLongValue(rawStats, "resolvedFeedback"));
        stats.put("closedFeedback", getLongValue(rawStats, "closedFeedback"));
        stats.put("unreadNotifications", getLongValue(rawStats, "unreadNotifications"));
        return stats;
    }

    private long getLongValue(Map<String, Object> data, String key)
    {
        if (data == null)
        {
            return 0L;
        }
        Object value = data.get(key);
        if (value instanceof Number)
        {
            return ((Number) value).longValue();
        }
        if (value == null)
        {
            return 0L;
        }
        try
        {
            return Long.parseLong(String.valueOf(value));
        }
        catch (NumberFormatException ex)
        {
            return 0L;
        }
    }

    private String getStringValue(Object value, String defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? defaultValue : text;
    }
}
