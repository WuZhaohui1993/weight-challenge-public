package com.ruoyi.weightcom.ruoyi.system.controller;

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
import com.ruoyi.weightcom.ruoyi.system.domain.WeightFeedLike;
import com.ruoyi.weightcom.ruoyi.system.service.IWeightFeedLikeService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 动态点赞Controller
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@RestController
@RequestMapping("/ruoyi-weightsystem/like")
public class WeightFeedLikeController extends BaseController
{
    @Autowired
    private IWeightFeedLikeService weightFeedLikeService;

    /**
     * 查询动态点赞列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weightsystem:like:list')")
    @GetMapping("/list")
    public TableDataInfo list(WeightFeedLike weightFeedLike)
    {
        startPage();
        List<WeightFeedLike> list = weightFeedLikeService.selectWeightFeedLikeList(weightFeedLike);
        return getDataTable(list);
    }

    /**
     * 导出动态点赞列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weightsystem:like:export')")
    @Log(title = "动态点赞", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WeightFeedLike weightFeedLike)
    {
        List<WeightFeedLike> list = weightFeedLikeService.selectWeightFeedLikeList(weightFeedLike);
        ExcelUtil<WeightFeedLike> util = new ExcelUtil<WeightFeedLike>(WeightFeedLike.class);
        util.exportExcel(response, list, "动态点赞数据");
    }

    /**
     * 获取动态点赞详细信息
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weightsystem:like:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(weightFeedLikeService.selectWeightFeedLikeById(id));
    }

    /**
     * 新增动态点赞
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weightsystem:like:add')")
    @Log(title = "动态点赞", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WeightFeedLike weightFeedLike)
    {
        return toAjax(weightFeedLikeService.insertWeightFeedLike(weightFeedLike));
    }

    /**
     * 修改动态点赞
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weightsystem:like:edit')")
    @Log(title = "动态点赞", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WeightFeedLike weightFeedLike)
    {
        return toAjax(weightFeedLikeService.updateWeightFeedLike(weightFeedLike));
    }

    /**
     * 删除动态点赞
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weightsystem:like:remove')")
    @Log(title = "动态点赞", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(weightFeedLikeService.deleteWeightFeedLikeByIds(ids));
    }
}
