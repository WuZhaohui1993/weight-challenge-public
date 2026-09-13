package com.ruoyi.weight.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.weight.service.IWeightDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 业务仪表盘统计接口
 * 
 * @author ruoyi
 * @date 2026-01-31
 */
@RestController
@RequestMapping("/ruoyi-weight/dashboard")
public class WeightDashboardController extends BaseController {

    @Autowired
    private IWeightDashboardService weightDashboardService;

    /**
     * 获取仪表盘概览数据
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:dashboard:list')")
    @GetMapping("/overview")
    public AjaxResult getOverview() {
        return success(weightDashboardService.getOverview());
    }

    /**
     * 获取最近7天打卡趋势
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:dashboard:list')")
    @GetMapping("/checkin-trend")
    public AjaxResult getCheckinTrend() {
        return success(weightDashboardService.getCheckinTrend());
    }

    /**
     * 获取热门圈子 Top5
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:dashboard:list')")
    @GetMapping("/top-circles")
    public AjaxResult getTopCircles() {
        return success(weightDashboardService.getTopCircles());
    }

    /**
     * 获取内容运营统计
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:dashboard:list')")
    @GetMapping("/content-stats")
    public AjaxResult getContentStats() {
        return success(weightDashboardService.getContentStats());
    }
}
