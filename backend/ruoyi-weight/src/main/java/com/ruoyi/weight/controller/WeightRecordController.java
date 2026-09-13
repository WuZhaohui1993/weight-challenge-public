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
import com.ruoyi.weight.domain.WeightRecord;
import com.ruoyi.weight.service.IWeightRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 体重记录Controller
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@RestController
@RequestMapping("/ruoyi-weight/weight-record")
public class WeightRecordController extends BaseController
{
    @Autowired
    private IWeightRecordService weightRecordService;

    /**
     * 查询体重记录列表
     */
    @PreAuthorize("@ss.hasAnyPermi('ruoyi-weight:record:list,ruoyi-weight:records:list')")
    @GetMapping("/list")
    public TableDataInfo list(WeightRecord weightRecord)
    {
        startPage();
        List<WeightRecord> list = weightRecordService.selectWeightRecordList(weightRecord);
        return getDataTable(list);
    }

    /**
     * 导出体重记录列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:record:export')")
    @Log(title = "体重记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WeightRecord weightRecord)
    {
        List<WeightRecord> list = weightRecordService.selectWeightRecordList(weightRecord);
        ExcelUtil<WeightRecord> util = new ExcelUtil<WeightRecord>(WeightRecord.class);
        util.exportExcel(response, list, "体重记录数据");
    }

    /**
     * 获取体重记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:record:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(weightRecordService.selectWeightRecordById(id));
    }

    /**
     * 新增体重记录
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:record:add')")
    @Log(title = "体重记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WeightRecord weightRecord)
    {
        return toAjax(weightRecordService.insertWeightRecord(weightRecord));
    }

    /**
     * 修改体重记录
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:record:edit')")
    @Log(title = "体重记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WeightRecord weightRecord)
    {
        return toAjax(weightRecordService.updateWeightRecord(weightRecord));
    }

    /**
     * 删除体重记录
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:record:remove')")
    @Log(title = "体重记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(weightRecordService.deleteWeightRecordByIds(ids));
    }
}
