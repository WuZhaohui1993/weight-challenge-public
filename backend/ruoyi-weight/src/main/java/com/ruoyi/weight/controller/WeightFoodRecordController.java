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
import com.ruoyi.weight.domain.WeightFoodRecord;
import com.ruoyi.weight.service.IWeightFoodRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 饮食记录Controller
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@RestController
@RequestMapping("/ruoyi-weight/food-record")
public class WeightFoodRecordController extends BaseController
{
    @Autowired
    private IWeightFoodRecordService weightFoodRecordService;

    /**
     * 查询饮食记录列表
     */
    @PreAuthorize("@ss.hasAnyPermi('ruoyi-weight:record:list,ruoyi-weight:records:list')")
    @GetMapping("/list")
    public TableDataInfo list(WeightFoodRecord weightFoodRecord)
    {
        startPage();
        List<WeightFoodRecord> list = weightFoodRecordService.selectWeightFoodRecordList(weightFoodRecord);
        return getDataTable(list);
    }

    /**
     * 导出饮食记录列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:record:export')")
    @Log(title = "饮食记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WeightFoodRecord weightFoodRecord)
    {
        List<WeightFoodRecord> list = weightFoodRecordService.selectWeightFoodRecordList(weightFoodRecord);
        ExcelUtil<WeightFoodRecord> util = new ExcelUtil<WeightFoodRecord>(WeightFoodRecord.class);
        util.exportExcel(response, list, "饮食记录数据");
    }

    /**
     * 获取饮食记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:record:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(weightFoodRecordService.selectWeightFoodRecordById(id));
    }

    /**
     * 新增饮食记录
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:record:add')")
    @Log(title = "饮食记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WeightFoodRecord weightFoodRecord)
    {
        return toAjax(weightFoodRecordService.insertWeightFoodRecord(weightFoodRecord));
    }

    /**
     * 修改饮食记录
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:record:edit')")
    @Log(title = "饮食记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WeightFoodRecord weightFoodRecord)
    {
        return toAjax(weightFoodRecordService.updateWeightFoodRecord(weightFoodRecord));
    }

    /**
     * 删除饮食记录
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:record:remove')")
    @Log(title = "饮食记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(weightFoodRecordService.deleteWeightFoodRecordByIds(ids));
    }
}
