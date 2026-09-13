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
import com.ruoyi.weight.domain.WeightComment;
import com.ruoyi.weight.service.IWeightCommentService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 评论Controller
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@RestController
@RequestMapping("/ruoyi-weight/comment")
public class WeightCommentController extends BaseController
{
    @Autowired
    private IWeightCommentService weightCommentService;

    /**
     * 查询评论列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:comment:list')")
    @GetMapping("/list")
    public TableDataInfo list(WeightComment weightComment)
    {
        startPage();
        List<WeightComment> list = weightCommentService.selectWeightCommentList(weightComment);
        return getDataTable(list);
    }

    /**
     * 导出评论列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:comment:export')")
    @Log(title = "评论", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WeightComment weightComment)
    {
        List<WeightComment> list = weightCommentService.selectWeightCommentList(weightComment);
        ExcelUtil<WeightComment> util = new ExcelUtil<WeightComment>(WeightComment.class);
        util.exportExcel(response, list, "评论数据");
    }

    /**
     * 获取评论详细信息
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:comment:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(weightCommentService.selectWeightCommentById(id));
    }

    /**
     * 新增评论
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:comment:add')")
    @Log(title = "评论", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WeightComment weightComment)
    {
        return toAjax(weightCommentService.insertWeightComment(weightComment));
    }

    /**
     * 修改评论
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:comment:edit')")
    @Log(title = "评论", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WeightComment weightComment)
    {
        return toAjax(weightCommentService.updateWeightComment(weightComment));
    }

    /**
     * 删除评论
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:comment:remove')")
    @Log(title = "评论", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(weightCommentService.deleteWeightCommentByIds(ids));
    }
}
