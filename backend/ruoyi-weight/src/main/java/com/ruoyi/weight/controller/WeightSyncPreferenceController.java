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
import com.ruoyi.weight.domain.WeightSyncPreference;
import com.ruoyi.weight.service.IWeightSyncPreferenceService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 同步偏好设置Controller
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@RestController
@RequestMapping("/ruoyi-weight/preference")
public class WeightSyncPreferenceController extends BaseController
{
    @Autowired
    private IWeightSyncPreferenceService weightSyncPreferenceService;

    /**
     * 查询同步偏好设置列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:preference:list')")
    @GetMapping("/list")
    public TableDataInfo list(WeightSyncPreference weightSyncPreference)
    {
        startPage();
        List<WeightSyncPreference> list = weightSyncPreferenceService.selectWeightSyncPreferenceList(weightSyncPreference);
        return getDataTable(list);
    }

    /**
     * 导出同步偏好设置列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:preference:export')")
    @Log(title = "同步偏好设置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WeightSyncPreference weightSyncPreference)
    {
        List<WeightSyncPreference> list = weightSyncPreferenceService.selectWeightSyncPreferenceList(weightSyncPreference);
        ExcelUtil<WeightSyncPreference> util = new ExcelUtil<WeightSyncPreference>(WeightSyncPreference.class);
        util.exportExcel(response, list, "同步偏好设置数据");
    }

    /**
     * 获取同步偏好设置详细信息
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:preference:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(weightSyncPreferenceService.selectWeightSyncPreferenceById(id));
    }

    /**
     * 新增同步偏好设置
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:preference:add')")
    @Log(title = "同步偏好设置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WeightSyncPreference weightSyncPreference)
    {
        return toAjax(weightSyncPreferenceService.insertWeightSyncPreference(weightSyncPreference));
    }

    /**
     * 修改同步偏好设置
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:preference:edit')")
    @Log(title = "同步偏好设置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WeightSyncPreference weightSyncPreference)
    {
        return toAjax(weightSyncPreferenceService.updateWeightSyncPreference(weightSyncPreference));
    }

    /**
     * 删除同步偏好设置
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:preference:remove')")
    @Log(title = "同步偏好设置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(weightSyncPreferenceService.deleteWeightSyncPreferenceByIds(ids));
    }
}
