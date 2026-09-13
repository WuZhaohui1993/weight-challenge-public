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
import com.ruoyi.weight.domain.WeightCommentLike;
import com.ruoyi.weight.service.IWeightCommentLikeService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 评论点赞Controller
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@RestController
@RequestMapping("/ruoyi-weight/like")
public class WeightCommentLikeController extends BaseController
{
    @Autowired
    private IWeightCommentLikeService weightCommentLikeService;

    /**
     * 查询评论点赞列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:like:list')")
    @GetMapping("/list")
    public TableDataInfo list(WeightCommentLike weightCommentLike)
    {
        startPage();
        List<WeightCommentLike> list = weightCommentLikeService.selectWeightCommentLikeList(weightCommentLike);
        return getDataTable(list);
    }

    /**
     * 导出评论点赞列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:like:export')")
    @Log(title = "评论点赞", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WeightCommentLike weightCommentLike)
    {
        List<WeightCommentLike> list = weightCommentLikeService.selectWeightCommentLikeList(weightCommentLike);
        ExcelUtil<WeightCommentLike> util = new ExcelUtil<WeightCommentLike>(WeightCommentLike.class);
        util.exportExcel(response, list, "评论点赞数据");
    }

    /**
     * 获取评论点赞详细信息
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:like:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(weightCommentLikeService.selectWeightCommentLikeById(id));
    }

    /**
     * 新增评论点赞
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:like:add')")
    @Log(title = "评论点赞", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WeightCommentLike weightCommentLike)
    {
        return toAjax(weightCommentLikeService.insertWeightCommentLike(weightCommentLike));
    }

    /**
     * 修改评论点赞
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:like:edit')")
    @Log(title = "评论点赞", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WeightCommentLike weightCommentLike)
    {
        return toAjax(weightCommentLikeService.updateWeightCommentLike(weightCommentLike));
    }

    /**
     * 删除评论点赞
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:like:remove')")
    @Log(title = "评论点赞", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(weightCommentLikeService.deleteWeightCommentLikeByIds(ids));
    }
}
