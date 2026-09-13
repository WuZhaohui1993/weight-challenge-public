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
import com.ruoyi.weight.domain.WeightCircleGoal;
import com.ruoyi.weight.service.IWeightCircleGoalService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 圈子目标Controller
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@RestController
@RequestMapping("/ruoyi-weight/goal")
public class WeightCircleGoalController extends BaseController
{
    @Autowired
    private IWeightCircleGoalService weightCircleGoalService;

    /**
     * 查询圈子目标列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:goal:list')")
    @GetMapping("/list")
    public TableDataInfo list(WeightCircleGoal weightCircleGoal)
    {
        startPage();
        List<WeightCircleGoal> list = weightCircleGoalService.selectWeightCircleGoalList(weightCircleGoal);
        return getDataTable(list);
    }

    /**
     * 导出圈子目标列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:goal:export')")
    @Log(title = "圈子目标", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WeightCircleGoal weightCircleGoal)
    {
        List<WeightCircleGoal> list = weightCircleGoalService.selectWeightCircleGoalList(weightCircleGoal);
        ExcelUtil<WeightCircleGoal> util = new ExcelUtil<WeightCircleGoal>(WeightCircleGoal.class);
        util.exportExcel(response, list, "圈子目标数据");
    }

    /**
     * 获取圈子目标详细信息
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:goal:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(weightCircleGoalService.selectWeightCircleGoalById(id));
    }

    /**
     * 新增圈子目标
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:goal:add')")
    @Log(title = "圈子目标", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WeightCircleGoal weightCircleGoal)
    {
        return toAjax(weightCircleGoalService.insertWeightCircleGoal(weightCircleGoal));
    }

    /**
     * 修改圈子目标
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:goal:edit')")
    @Log(title = "圈子目标", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WeightCircleGoal weightCircleGoal)
    {
        return toAjax(weightCircleGoalService.updateWeightCircleGoal(weightCircleGoal));
    }

    /**
     * 删除圈子目标
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:goal:remove')")
    @Log(title = "圈子目标", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(weightCircleGoalService.deleteWeightCircleGoalByIds(ids));
    }
}
