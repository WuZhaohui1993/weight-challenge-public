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
import com.ruoyi.weight.domain.WeightHabit;
import com.ruoyi.weight.service.IWeightHabitService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 习惯定义Controller
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@RestController
@RequestMapping("/ruoyi-weight/habit")
public class WeightHabitController extends BaseController
{
    @Autowired
    private IWeightHabitService weightHabitService;

    /**
     * 查询习惯定义列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:habit:list')")
    @GetMapping("/list")
    public TableDataInfo list(WeightHabit weightHabit)
    {
        startPage();
        List<WeightHabit> list = weightHabitService.selectWeightHabitList(weightHabit);
        return getDataTable(list);
    }

    /**
     * 导出习惯定义列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:habit:export')")
    @Log(title = "习惯定义", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WeightHabit weightHabit)
    {
        List<WeightHabit> list = weightHabitService.selectWeightHabitList(weightHabit);
        ExcelUtil<WeightHabit> util = new ExcelUtil<WeightHabit>(WeightHabit.class);
        util.exportExcel(response, list, "习惯定义数据");
    }

    /**
     * 获取习惯定义详细信息
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:habit:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(weightHabitService.selectWeightHabitById(id));
    }

    /**
     * 新增习惯定义
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:habit:add')")
    @Log(title = "习惯定义", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WeightHabit weightHabit)
    {
        return toAjax(weightHabitService.insertWeightHabit(weightHabit));
    }

    /**
     * 修改习惯定义
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:habit:edit')")
    @Log(title = "习惯定义", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WeightHabit weightHabit)
    {
        return toAjax(weightHabitService.updateWeightHabit(weightHabit));
    }

    /**
     * 删除习惯定义
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:habit:remove')")
    @Log(title = "习惯定义", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(weightHabitService.deleteWeightHabitByIds(ids));
    }
}
