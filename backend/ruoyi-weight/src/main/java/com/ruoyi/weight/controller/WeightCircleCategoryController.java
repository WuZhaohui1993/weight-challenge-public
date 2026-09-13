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
import com.ruoyi.weight.domain.WeightCircleCategory;
import com.ruoyi.weight.service.IWeightCircleCategoryService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 圈子分类Controller
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@RestController
@RequestMapping("/ruoyi-weight/category")
public class WeightCircleCategoryController extends BaseController
{
    @Autowired
    private IWeightCircleCategoryService weightCircleCategoryService;

    /**
     * 查询圈子分类列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:category:list')")
    @GetMapping("/list")
    public TableDataInfo list(WeightCircleCategory weightCircleCategory)
    {
        startPage();
        List<WeightCircleCategory> list = weightCircleCategoryService.selectWeightCircleCategoryList(weightCircleCategory);
        return getDataTable(list);
    }

    /**
     * 导出圈子分类列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:category:export')")
    @Log(title = "圈子分类", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WeightCircleCategory weightCircleCategory)
    {
        List<WeightCircleCategory> list = weightCircleCategoryService.selectWeightCircleCategoryList(weightCircleCategory);
        ExcelUtil<WeightCircleCategory> util = new ExcelUtil<WeightCircleCategory>(WeightCircleCategory.class);
        util.exportExcel(response, list, "圈子分类数据");
    }

    /**
     * 获取圈子分类详细信息
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:category:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(weightCircleCategoryService.selectWeightCircleCategoryById(id));
    }

    /**
     * 新增圈子分类
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:category:add')")
    @Log(title = "圈子分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WeightCircleCategory weightCircleCategory)
    {
        return toAjax(weightCircleCategoryService.insertWeightCircleCategory(weightCircleCategory));
    }

    /**
     * 修改圈子分类
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:category:edit')")
    @Log(title = "圈子分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WeightCircleCategory weightCircleCategory)
    {
        return toAjax(weightCircleCategoryService.updateWeightCircleCategory(weightCircleCategory));
    }

    /**
     * 删除圈子分类
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:category:remove')")
    @Log(title = "圈子分类", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(weightCircleCategoryService.deleteWeightCircleCategoryByIds(ids));
    }
}
