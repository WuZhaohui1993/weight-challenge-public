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
import com.ruoyi.weight.domain.WeightCircle;
import com.ruoyi.weight.service.IWeightCircleService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 圈子主Controller
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@RestController
@RequestMapping("/ruoyi-weight/circle")
public class WeightCircleController extends BaseController
{
    @Autowired
    private IWeightCircleService weightCircleService;

    /**
     * 查询圈子主列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:circle:list')")
    @GetMapping("/list")
    public TableDataInfo list(WeightCircle weightCircle)
    {
        startPage();
        List<WeightCircle> list = weightCircleService.selectWeightCircleList(weightCircle);
        return getDataTable(list);
    }

    /**
     * 导出圈子主列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:circle:export')")
    @Log(title = "圈子主", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WeightCircle weightCircle)
    {
        List<WeightCircle> list = weightCircleService.selectWeightCircleList(weightCircle);
        ExcelUtil<WeightCircle> util = new ExcelUtil<WeightCircle>(WeightCircle.class);
        util.exportExcel(response, list, "圈子主数据");
    }

    /**
     * 获取圈子主详细信息
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:circle:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(weightCircleService.selectWeightCircleById(id));
    }

    /**
     * 新增圈子主
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:circle:add')")
    @Log(title = "圈子主", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WeightCircle weightCircle)
    {
        return toAjax(weightCircleService.insertWeightCircle(weightCircle));
    }

    /**
     * 修改圈子主
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:circle:edit')")
    @Log(title = "圈子主", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WeightCircle weightCircle)
    {
        return toAjax(weightCircleService.updateWeightCircle(weightCircle));
    }

    /**
     * 删除圈子主
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:circle:remove')")
    @Log(title = "圈子主", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(weightCircleService.deleteWeightCircleByIds(ids));
    }
}
