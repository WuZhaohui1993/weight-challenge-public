package com.ruoyi.weight.controller;

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.weight.domain.WeightFeedback;
import com.ruoyi.weight.domain.WeightNotification;
import com.ruoyi.weight.service.IWeightFeedbackService;
import com.ruoyi.weight.service.IWeightNotificationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户反馈Controller
 *
 * @author ruoyi
 * @date 2026-04-26
 */
@RestController
@RequestMapping("/ruoyi-weight/feedback")
public class WeightFeedbackController extends BaseController
{
    @Autowired
    private IWeightFeedbackService weightFeedbackService;

    @Autowired
    private IWeightNotificationService notificationService;

    /**
     * 查询用户反馈列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:feedback:list')")
    @GetMapping("/list")
    public TableDataInfo list(WeightFeedback weightFeedback)
    {
        startPage();
        List<WeightFeedback> list = weightFeedbackService.selectWeightFeedbackList(weightFeedback);
        return getDataTable(list);
    }

    /**
     * 导出用户反馈列表
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:feedback:export')")
    @Log(title = "用户反馈", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WeightFeedback weightFeedback)
    {
        List<WeightFeedback> list = weightFeedbackService.selectWeightFeedbackList(weightFeedback);
        ExcelUtil<WeightFeedback> util = new ExcelUtil<WeightFeedback>(WeightFeedback.class);
        util.exportExcel(response, list, "用户反馈数据");
    }

    /**
     * 获取用户反馈详细信息
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:feedback:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(weightFeedbackService.selectWeightFeedbackById(id));
    }

    /**
     * 新增用户反馈
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:feedback:add')")
    @Log(title = "用户反馈", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WeightFeedback weightFeedback)
    {
        weightFeedback.setCreateBy(getUsername());
        normalizeFeedbackForAdmin(weightFeedback, false);
        return toAjax(weightFeedbackService.insertWeightFeedback(weightFeedback));
    }

    /**
     * 修改用户反馈
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:feedback:edit')")
    @Log(title = "用户反馈", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WeightFeedback weightFeedback)
    {
        if (weightFeedback == null || weightFeedback.getId() == null)
        {
            return error("反馈ID不能为空");
        }

        WeightFeedback previous = weightFeedbackService.selectWeightFeedbackById(weightFeedback.getId());
        if (previous == null)
        {
            return error("反馈不存在");
        }

        weightFeedback.setUpdateBy(getUsername());
        normalizeFeedbackForAdmin(weightFeedback, true);
        int rows = weightFeedbackService.updateWeightFeedback(weightFeedback);
        if (rows > 0)
        {
            WeightFeedback updated = weightFeedbackService.selectWeightFeedbackById(weightFeedback.getId());
            maybeCreateFeedbackNotification(previous, updated);
        }
        return toAjax(rows);
    }

    /**
     * 删除用户反馈
     */
    @PreAuthorize("@ss.hasPermi('ruoyi-weight:feedback:remove')")
    @Log(title = "用户反馈", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(weightFeedbackService.deleteWeightFeedbackByIds(ids));
    }

    private void normalizeFeedbackForAdmin(WeightFeedback feedback, boolean editing)
    {
        if (StringUtils.isBlank(feedback.getStatus()))
        {
            feedback.setStatus("pending");
        }
        if (StringUtils.isBlank(feedback.getPriority()))
        {
            feedback.setPriority("normal");
        }
        if (StringUtils.isNotBlank(feedback.getReplyContent()))
        {
            if (StringUtils.isBlank(feedback.getReplyBy()))
            {
                feedback.setReplyBy(getUsername());
            }
            if (feedback.getReplyTime() == null)
            {
                feedback.setReplyTime(new Date());
            }
        }
        else if (!editing)
        {
            feedback.setReplyContent(null);
            feedback.setReplyBy(null);
            feedback.setReplyTime(null);
        }
    }

    private void maybeCreateFeedbackNotification(WeightFeedback previous, WeightFeedback updated)
    {
        if (previous == null || updated == null || updated.getUserId() == null)
        {
            return;
        }

        boolean statusChanged = !StringUtils.equals(previous.getStatus(), updated.getStatus());
        boolean replyChanged = !StringUtils.equals(
            StringUtils.defaultString(previous.getReplyContent()),
            StringUtils.defaultString(updated.getReplyContent())
        );
        if (!statusChanged && !replyChanged)
        {
            return;
        }

        String content = StringUtils.isNotBlank(updated.getReplyContent())
            ? "你的反馈已有处理回复：" + updated.getReplyContent()
            : "你的反馈处理状态已更新为：" + toStatusText(updated.getStatus());
        WeightNotification notification = new WeightNotification();
        notification.setUserId(updated.getUserId());
        notification.setType("system");
        notification.setFromUserId(null);
        notification.setTargetType("feedback");
        notification.setTargetId(updated.getId());
        notification.setContent(content);
        notification.setPreview(StringUtils.abbreviate(content, 80));
        notification.setIsRead(0);
        notificationService.insertWeightNotification(notification);
    }

    private String toStatusText(String status)
    {
        if ("pending".equals(status))
        {
            return "待处理";
        }
        if ("processing".equals(status))
        {
            return "处理中";
        }
        if ("resolved".equals(status))
        {
            return "已解决";
        }
        if ("closed".equals(status))
        {
            return "已关闭";
        }
        return StringUtils.defaultIfBlank(status, "未知");
    }
}
