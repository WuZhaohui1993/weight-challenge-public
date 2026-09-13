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
import com.ruoyi.weight.domain.WeightDailyTask;
import com.ruoyi.weight.service.IWeightDailyTaskService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 每日任务Controller
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@RestController
@RequestMapping("/ruoyi-weight/task")
public class WeightDailyTaskController extends BaseController
{
    @Autowired
    private IWeightDailyTaskService weightDailyTaskService;

    /**
     * 查询每日任务列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:task:list')")
    @GetMapping("/list")
    public TableDataInfo list(WeightDailyTask weightDailyTask)
    {
        startPage();
        List<WeightDailyTask> list = weightDailyTaskService.selectWeightDailyTaskList(weightDailyTask);
        return getDataTable(list);
    }

    /**
     * 导出每日任务列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:task:export')")
    @Log(title = "每日任务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WeightDailyTask weightDailyTask)
    {
        List<WeightDailyTask> list = weightDailyTaskService.selectWeightDailyTaskList(weightDailyTask);
        ExcelUtil<WeightDailyTask> util = new ExcelUtil<WeightDailyTask>(WeightDailyTask.class);
        util.exportExcel(response, list, "每日任务数据");
    }

    /**
     * 获取每日任务详细信息
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:task:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(weightDailyTaskService.selectWeightDailyTaskById(id));
    }

    /**
     * 新增每日任务
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:task:add')")
    @Log(title = "每日任务", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WeightDailyTask weightDailyTask)
    {
        return toAjax(weightDailyTaskService.insertWeightDailyTask(weightDailyTask));
    }

    /**
     * 修改每日任务
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:task:edit')")
    @Log(title = "每日任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WeightDailyTask weightDailyTask)
    {
        return toAjax(weightDailyTaskService.updateWeightDailyTask(weightDailyTask));
    }

    /**
     * 删除每日任务
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:task:remove')")
    @Log(title = "每日任务", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(weightDailyTaskService.deleteWeightDailyTaskByIds(ids));
    }
}
