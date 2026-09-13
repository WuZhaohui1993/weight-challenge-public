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
import com.ruoyi.weight.domain.WeightWaterRecord;
import com.ruoyi.weight.service.IWeightWaterRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 饮水记录Controller
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@RestController
@RequestMapping("/ruoyi-weight/water-record")
public class WeightWaterRecordController extends BaseController
{
    @Autowired
    private IWeightWaterRecordService weightWaterRecordService;

    /**
     * 查询饮水记录列表
     */
    @PreAuthorize("@ss.hasAnyPermi('ruoyi-weight:record:list,ruoyi-weight:records:list')")
    @GetMapping("/list")
    public TableDataInfo list(WeightWaterRecord weightWaterRecord)
    {
        startPage();
        List<WeightWaterRecord> list = weightWaterRecordService.selectWeightWaterRecordList(weightWaterRecord);
        return getDataTable(list);
    }

    /**
     * 导出饮水记录列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:record:export')")
    @Log(title = "饮水记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WeightWaterRecord weightWaterRecord)
    {
        List<WeightWaterRecord> list = weightWaterRecordService.selectWeightWaterRecordList(weightWaterRecord);
        ExcelUtil<WeightWaterRecord> util = new ExcelUtil<WeightWaterRecord>(WeightWaterRecord.class);
        util.exportExcel(response, list, "饮水记录数据");
    }

    /**
     * 获取饮水记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:record:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(weightWaterRecordService.selectWeightWaterRecordById(id));
    }

    /**
     * 新增饮水记录
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:record:add')")
    @Log(title = "饮水记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WeightWaterRecord weightWaterRecord)
    {
        return toAjax(weightWaterRecordService.insertWeightWaterRecord(weightWaterRecord));
    }

    /**
     * 修改饮水记录
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:record:edit')")
    @Log(title = "饮水记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WeightWaterRecord weightWaterRecord)
    {
        return toAjax(weightWaterRecordService.updateWeightWaterRecord(weightWaterRecord));
    }

    /**
     * 删除饮水记录
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:record:remove')")
    @Log(title = "饮水记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(weightWaterRecordService.deleteWeightWaterRecordByIds(ids));
    }
}
