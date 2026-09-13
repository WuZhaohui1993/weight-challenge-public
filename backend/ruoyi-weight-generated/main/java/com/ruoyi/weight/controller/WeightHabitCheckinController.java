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
import com.ruoyi.weight.domain.WeightHabitCheckin;
import com.ruoyi.weight.service.IWeightHabitCheckinService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 习惯打卡记录Controller
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@RestController
@RequestMapping("/ruoyi-weight/checkin")
public class WeightHabitCheckinController extends BaseController
{
    @Autowired
    private IWeightHabitCheckinService weightHabitCheckinService;

    /**
     * 查询习惯打卡记录列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:checkin:list')")
    @GetMapping("/list")
    public TableDataInfo list(WeightHabitCheckin weightHabitCheckin)
    {
        startPage();
        List<WeightHabitCheckin> list = weightHabitCheckinService.selectWeightHabitCheckinList(weightHabitCheckin);
        return getDataTable(list);
    }

    /**
     * 导出习惯打卡记录列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:checkin:export')")
    @Log(title = "习惯打卡记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WeightHabitCheckin weightHabitCheckin)
    {
        List<WeightHabitCheckin> list = weightHabitCheckinService.selectWeightHabitCheckinList(weightHabitCheckin);
        ExcelUtil<WeightHabitCheckin> util = new ExcelUtil<WeightHabitCheckin>(WeightHabitCheckin.class);
        util.exportExcel(response, list, "习惯打卡记录数据");
    }

    /**
     * 获取习惯打卡记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:checkin:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(weightHabitCheckinService.selectWeightHabitCheckinById(id));
    }

    /**
     * 新增习惯打卡记录
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:checkin:add')")
    @Log(title = "习惯打卡记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WeightHabitCheckin weightHabitCheckin)
    {
        return toAjax(weightHabitCheckinService.insertWeightHabitCheckin(weightHabitCheckin));
    }

    /**
     * 修改习惯打卡记录
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:checkin:edit')")
    @Log(title = "习惯打卡记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WeightHabitCheckin weightHabitCheckin)
    {
        return toAjax(weightHabitCheckinService.updateWeightHabitCheckin(weightHabitCheckin));
    }

    /**
     * 删除习惯打卡记录
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:checkin:remove')")
    @Log(title = "习惯打卡记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(weightHabitCheckinService.deleteWeightHabitCheckinByIds(ids));
    }
}
