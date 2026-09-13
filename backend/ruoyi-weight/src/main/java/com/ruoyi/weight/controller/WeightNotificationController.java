package com.ruoyi.weight.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.weight.domain.WeightNotification;
import com.ruoyi.weight.domain.WeightNotificationSendRequest;
import com.ruoyi.weight.service.IWeightNotificationService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 消息通知Controller
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@RestController
@RequestMapping("/ruoyi-weight/notification")
public class WeightNotificationController extends BaseController
{
    @Autowired
    private IWeightNotificationService weightNotificationService;

    /**
     * 查询消息通知列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:notification:list')")
    @GetMapping("/list")
    public TableDataInfo list(WeightNotification weightNotification)
    {
        startPage();
        List<WeightNotification> list = weightNotificationService.selectWeightNotificationList(weightNotification);
        return getDataTable(list);
    }

    /**
     * 导出消息通知列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:notification:export')")
    @Log(title = "消息通知", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WeightNotification weightNotification)
    {
        List<WeightNotification> list = weightNotificationService.selectWeightNotificationList(weightNotification);
        ExcelUtil<WeightNotification> util = new ExcelUtil<WeightNotification>(WeightNotification.class);
        util.exportExcel(response, list, "消息通知数据");
    }

    /**
     * 获取消息通知详细信息
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:notification:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(weightNotificationService.selectWeightNotificationById(id));
    }

    /**
     * 新增消息通知
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:notification:add')")
    @Log(title = "消息通知", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WeightNotification weightNotification)
    {
        return toAjax(weightNotificationService.insertWeightNotification(weightNotification));
    }

    /**
     * 管理端发送系统通知
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:notification:add')")
    @Log(title = "系统通知发送", businessType = BusinessType.INSERT)
    @PostMapping("/broadcast")
    public AjaxResult broadcast(@RequestBody WeightNotificationSendRequest request)
    {
        int count = weightNotificationService.sendSystemNotification(request);
        AjaxResult result = success("已发送 " + count + " 条系统通知");
        result.put("count", count);
        return result;
    }

    /**
     * 修改消息通知
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:notification:edit')")
    @Log(title = "消息通知", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WeightNotification weightNotification)
    {
        return toAjax(weightNotificationService.updateWeightNotification(weightNotification));
    }

    /**
     * 删除消息通知
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:notification:remove')")
    @Log(title = "消息通知", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(weightNotificationService.deleteWeightNotificationByIds(ids));
    }
}
