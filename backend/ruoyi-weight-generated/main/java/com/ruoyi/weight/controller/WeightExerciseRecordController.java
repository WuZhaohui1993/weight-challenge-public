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
import com.ruoyi.weight.domain.WeightExerciseRecord;
import com.ruoyi.weight.service.IWeightExerciseRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 运动记录Controller
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@RestController
@RequestMapping("/ruoyi-weight/record")
public class WeightExerciseRecordController extends BaseController
{
    @Autowired
    private IWeightExerciseRecordService weightExerciseRecordService;

    /**
     * 查询运动记录列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:record:list')")
    @GetMapping("/list")
    public TableDataInfo list(WeightExerciseRecord weightExerciseRecord)
    {
        startPage();
        List<WeightExerciseRecord> list = weightExerciseRecordService.selectWeightExerciseRecordList(weightExerciseRecord);
        return getDataTable(list);
    }

    /**
     * 导出运动记录列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:record:export')")
    @Log(title = "运动记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WeightExerciseRecord weightExerciseRecord)
    {
        List<WeightExerciseRecord> list = weightExerciseRecordService.selectWeightExerciseRecordList(weightExerciseRecord);
        ExcelUtil<WeightExerciseRecord> util = new ExcelUtil<WeightExerciseRecord>(WeightExerciseRecord.class);
        util.exportExcel(response, list, "运动记录数据");
    }

    /**
     * 获取运动记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:record:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(weightExerciseRecordService.selectWeightExerciseRecordById(id));
    }

    /**
     * 新增运动记录
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:record:add')")
    @Log(title = "运动记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WeightExerciseRecord weightExerciseRecord)
    {
        return toAjax(weightExerciseRecordService.insertWeightExerciseRecord(weightExerciseRecord));
    }

    /**
     * 修改运动记录
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:record:edit')")
    @Log(title = "运动记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WeightExerciseRecord weightExerciseRecord)
    {
        return toAjax(weightExerciseRecordService.updateWeightExerciseRecord(weightExerciseRecord));
    }

    /**
     * 删除运动记录
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:record:remove')")
    @Log(title = "运动记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(weightExerciseRecordService.deleteWeightExerciseRecordByIds(ids));
    }
}
