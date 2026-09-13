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
import com.ruoyi.weight.domain.WeightUserFollow;
import com.ruoyi.weight.service.IWeightUserFollowService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 用户关注关系Controller
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@RestController
@RequestMapping("/ruoyi-weight/follow")
public class WeightUserFollowController extends BaseController
{
    @Autowired
    private IWeightUserFollowService weightUserFollowService;

    /**
     * 查询用户关注关系列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:follow:list')")
    @GetMapping("/list")
    public TableDataInfo list(WeightUserFollow weightUserFollow)
    {
        startPage();
        List<WeightUserFollow> list = weightUserFollowService.selectWeightUserFollowList(weightUserFollow);
        return getDataTable(list);
    }

    /**
     * 导出用户关注关系列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:follow:export')")
    @Log(title = "用户关注关系", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WeightUserFollow weightUserFollow)
    {
        List<WeightUserFollow> list = weightUserFollowService.selectWeightUserFollowList(weightUserFollow);
        ExcelUtil<WeightUserFollow> util = new ExcelUtil<WeightUserFollow>(WeightUserFollow.class);
        util.exportExcel(response, list, "用户关注关系数据");
    }

    /**
     * 获取用户关注关系详细信息
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:follow:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(weightUserFollowService.selectWeightUserFollowById(id));
    }

    /**
     * 新增用户关注关系
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:follow:add')")
    @Log(title = "用户关注关系", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WeightUserFollow weightUserFollow)
    {
        return toAjax(weightUserFollowService.insertWeightUserFollow(weightUserFollow));
    }

    /**
     * 修改用户关注关系
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:follow:edit')")
    @Log(title = "用户关注关系", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WeightUserFollow weightUserFollow)
    {
        return toAjax(weightUserFollowService.updateWeightUserFollow(weightUserFollow));
    }

    /**
     * 删除用户关注关系
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:follow:remove')")
    @Log(title = "用户关注关系", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(weightUserFollowService.deleteWeightUserFollowByIds(ids));
    }
}
