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
import com.ruoyi.weight.domain.WeightAchievement;
import com.ruoyi.weight.service.IWeightAchievementService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 成就徽章Controller
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@RestController
@RequestMapping("/ruoyi-weight/achievement")
public class WeightAchievementController extends BaseController
{
    @Autowired
    private IWeightAchievementService weightAchievementService;

    /**
     * 查询成就徽章列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:achievement:list')")
    @GetMapping("/list")
    public TableDataInfo list(WeightAchievement weightAchievement)
    {
        startPage();
        List<WeightAchievement> list = weightAchievementService.selectWeightAchievementList(weightAchievement);
        return getDataTable(list);
    }

    /**
     * 导出成就徽章列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:achievement:export')")
    @Log(title = "成就徽章", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WeightAchievement weightAchievement)
    {
        List<WeightAchievement> list = weightAchievementService.selectWeightAchievementList(weightAchievement);
        ExcelUtil<WeightAchievement> util = new ExcelUtil<WeightAchievement>(WeightAchievement.class);
        util.exportExcel(response, list, "成就徽章数据");
    }

    /**
     * 获取成就徽章详细信息
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:achievement:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(weightAchievementService.selectWeightAchievementById(id));
    }

    /**
     * 新增成就徽章
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:achievement:add')")
    @Log(title = "成就徽章", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WeightAchievement weightAchievement)
    {
        return toAjax(weightAchievementService.insertWeightAchievement(weightAchievement));
    }

    /**
     * 修改成就徽章
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:achievement:edit')")
    @Log(title = "成就徽章", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WeightAchievement weightAchievement)
    {
        return toAjax(weightAchievementService.updateWeightAchievement(weightAchievement));
    }

    /**
     * 删除成就徽章
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:achievement:remove')")
    @Log(title = "成就徽章", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(weightAchievementService.deleteWeightAchievementByIds(ids));
    }
}
