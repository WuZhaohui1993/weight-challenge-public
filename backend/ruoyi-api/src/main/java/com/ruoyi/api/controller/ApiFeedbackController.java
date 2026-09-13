package com.ruoyi.api.controller;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.api.dto.ApiFeedbackCreateRequest;
import com.ruoyi.api.dto.ApiFeedbackResponse;
import com.ruoyi.api.service.WechatContentSecurityService;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import com.ruoyi.framework.config.ServerConfig;
import com.ruoyi.weight.domain.WeightFeedback;
import com.ruoyi.weight.service.IWeightFeedbackService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 移动端 - 用户反馈 API
 */
@RestController
@RequestMapping("/api/feedback")
public class ApiFeedbackController extends ApiBaseController {

    private static final int CONTENT_MIN_LENGTH = 10;
    private static final int CONTENT_MAX_LENGTH = 500;
    private static final int MAX_IMAGE_COUNT = 3;
    private static final int MAX_CONTACT_LENGTH = 80;
    private static final int MAX_SOURCE_PAGE_LENGTH = 160;
    private static final int MAX_ENVIRONMENT_LENGTH = 2000;
    private static final Set<String> CATEGORY_SET = new HashSet<>(Arrays.asList(
        "bug", "suggestion", "experience", "account", "other"
    ));

    @Autowired
    private IWeightFeedbackService feedbackService;

    @Autowired
    private ServerConfig serverConfig;

    @Autowired
    private WechatContentSecurityService contentSecurityService;

    @PostMapping("/upload-image")
    public AjaxResult uploadFeedbackImage(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }
        if (file == null || file.isEmpty()) {
            return AjaxResult.error("请选择图片文件");
        }

        try {
            String fileName = FileUploadUtils.upload(RuoYiConfig.getUploadPath(), file, MimeTypeUtils.IMAGE_EXTENSION, true);
            String securityMessage = contentSecurityService.validateImage(
                    resolveUploadedProfileFile(fileName), "feedback.image");
            if (securityMessage != null) {
                deleteUploadedProfileFile(fileName);
                return AjaxResult.error(securityMessage);
            }
            String url = serverConfig.getUrl() + fileName;

            AjaxResult ajax = AjaxResult.success("上传成功");
            ajax.put("url", url);
            ajax.put("fileName", fileName);
            ajax.put("newFileName", FileUtils.getName(fileName));
            ajax.put("originalFilename", file.getOriginalFilename());
            return ajax;
        } catch (Exception e) {
            return AjaxResult.error(resolveUploadErrorMessage(e, "图片"));
        }
    }

    @PostMapping
    public AjaxResult createFeedback(HttpServletRequest request, @RequestBody ApiFeedbackCreateRequest body) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }
        if (body == null) {
            return AjaxResult.error("请求参数不能为空");
        }

        String category = normalizeCategory(body.getCategory());
        if (category == null) {
            return AjaxResult.error("请选择反馈类型");
        }

        String content = StringUtils.trimToNull(body.getContent());
        if (content == null || content.length() < CONTENT_MIN_LENGTH) {
            return AjaxResult.error("反馈内容至少填写 10 个字");
        }
        if (content.length() > CONTENT_MAX_LENGTH) {
            return AjaxResult.error("反馈内容不能超过 500 个字");
        }
        String securityMessage = contentSecurityService.validateText(
                userId, content, WechatContentSecurityService.SCENE_COMMENT, "feedback.create");
        if (securityMessage != null) {
            return AjaxResult.error(securityMessage);
        }

        List<String> images = normalizeImages(body.getImages());
        if (images.size() > MAX_IMAGE_COUNT) {
            return AjaxResult.error("最多上传 3 张截图");
        }

        String contact = truncate(StringUtils.trimToNull(body.getContact()), MAX_CONTACT_LENGTH);
        String sourcePage = truncate(StringUtils.trimToNull(body.getSourcePage()), MAX_SOURCE_PAGE_LENGTH);
        String environmentJson = normalizeEnvironment(body.getEnvironment());

        WeightFeedback feedback = new WeightFeedback();
        feedback.setUserId(userId);
        feedback.setCategory(category);
        feedback.setContent(content);
        feedback.setImages(images.isEmpty() ? null : JSON.toJSONString(images));
        feedback.setContact(contact);
        feedback.setSourcePage(sourcePage);
        feedback.setEnvironmentJson(environmentJson);
        feedback.setStatus("pending");
        feedback.setPriority("normal");
        feedback.setCreateBy("miniapp");
        feedbackService.insertWeightFeedback(feedback);

        WeightFeedback saved = feedbackService.selectWeightFeedbackById(feedback.getId());
        return successWithData("反馈已提交", buildFeedbackResponse(saved));
    }

    @GetMapping("/my")
    public AjaxResult myFeedback(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }

        WeightFeedback query = new WeightFeedback();
        query.setUserId(userId);
        startPage();
        List<WeightFeedback> feedbackList = feedbackService.selectWeightFeedbackList(query);
        return pageSuccess(buildFeedbackItems(feedbackList));
    }

    @GetMapping("/{feedbackId}")
    public AjaxResult feedbackDetail(HttpServletRequest request, @PathVariable("feedbackId") Long feedbackId) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return unauthorized("未授权，请先登录");
        }

        WeightFeedback feedback = feedbackService.selectWeightFeedbackById(feedbackId);
        if (feedback == null || !userId.equals(feedback.getUserId())) {
            return AjaxResult.error("反馈不存在");
        }

        return successWithData("查询成功", buildFeedbackResponse(feedback));
    }

    private String normalizeCategory(String category) {
        String value = StringUtils.trimToEmpty(category);
        return CATEGORY_SET.contains(value) ? value : null;
    }

    private List<String> normalizeImages(List<String> rawImages) {
        if (rawImages == null || rawImages.isEmpty()) {
            return new ArrayList<>();
        }

        Set<String> images = new LinkedHashSet<>();
        for (String rawImage : rawImages) {
            String image = StringUtils.trimToNull(rawImage);
            if (image != null) {
                images.add(image);
            }
        }
        return new ArrayList<>(images);
    }

    private String normalizeEnvironment(Map<String, Object> environment) {
        if (environment == null || environment.isEmpty()) {
            return null;
        }
        String json = JSON.toJSONString(environment);
        return truncate(json, MAX_ENVIRONMENT_LENGTH);
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    private List<ApiFeedbackResponse> buildFeedbackItems(List<WeightFeedback> feedbackList) {
        List<ApiFeedbackResponse> items = new ArrayList<>();
        if (feedbackList == null) {
            return items;
        }
        for (WeightFeedback feedback : feedbackList) {
            items.add(buildFeedbackResponse(feedback));
        }
        return items;
    }

    private ApiFeedbackResponse buildFeedbackResponse(WeightFeedback feedback) {
        ApiFeedbackResponse response = new ApiFeedbackResponse();
        if (feedback == null) {
            return response;
        }

        response.setId(feedback.getId());
        response.setUserId(feedback.getUserId());
        response.setCategory(feedback.getCategory());
        response.setCategoryLabel(toCategoryLabel(feedback.getCategory()));
        response.setContent(feedback.getContent());
        response.setImages(parseImages(feedback.getImages()));
        response.setContact(feedback.getContact());
        response.setSourcePage(feedback.getSourcePage());
        response.setEnvironmentJson(feedback.getEnvironmentJson());
        response.setStatus(feedback.getStatus());
        response.setStatusLabel(toStatusLabel(feedback.getStatus()));
        response.setPriority(feedback.getPriority());
        response.setReplyContent(feedback.getReplyContent());
        response.setReplyBy(feedback.getReplyBy());
        response.setReplyTime(feedback.getReplyTime());
        response.setCreateTime(feedback.getCreateTime());
        response.setUpdateTime(feedback.getUpdateTime());
        return response;
    }

    private List<String> parseImages(String images) {
        if (StringUtils.isBlank(images)) {
            return new ArrayList<>();
        }
        try {
            List<String> parsed = JSON.parseArray(images, String.class);
            return parsed != null ? parsed : new ArrayList<>();
        } catch (Exception ignored) {
            List<String> fallback = new ArrayList<>();
            fallback.add(images);
            return fallback;
        }
    }

    private String toCategoryLabel(String category) {
        if ("bug".equals(category)) {
            return "Bug 问题";
        }
        if ("suggestion".equals(category)) {
            return "功能建议";
        }
        if ("experience".equals(category)) {
            return "体验问题";
        }
        if ("account".equals(category)) {
            return "账号数据";
        }
        return "其他";
    }

    private String toStatusLabel(String status) {
        if ("pending".equals(status)) {
            return "待处理";
        }
        if ("processing".equals(status)) {
            return "处理中";
        }
        if ("resolved".equals(status)) {
            return "已解决";
        }
        if ("closed".equals(status)) {
            return "已关闭";
        }
        return "未知";
    }
}
