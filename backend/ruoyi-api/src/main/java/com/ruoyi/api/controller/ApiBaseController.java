package com.ruoyi.api.controller;

import com.github.pagehelper.PageInfo;
import com.ruoyi.api.dto.ApiPageResponse;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * 移动端 API 控制器基类
 */
public abstract class ApiBaseController extends BaseController {

    private static final String DEFAULT_UPLOAD_LIMIT_TEXT = "10MB";
    private static final long DEFAULT_UPLOAD_LIMIT_BYTES = 10 * 1024 * 1024L;

    @Value("${spring.servlet.multipart.max-file-size:10MB}")
    private String maxUploadFileSize;

    protected Long getCurrentUserId(HttpServletRequest request) {
        Object userId = request.getAttribute("userId");
        return userId != null ? Long.valueOf(userId.toString()) : null;
    }

    protected AjaxResult successWithData(Object data) {
        return successWithData("操作成功", data);
    }

    protected AjaxResult successWithData(String message, Object data) {
        AjaxResult result = AjaxResult.success(message);
        result.put(AjaxResult.DATA_TAG, data);
        return result;
    }

    protected AjaxResult unauthorized(String message) {
        AjaxResult result = new AjaxResult(HttpStatus.UNAUTHORIZED, message);
        result.put(AjaxResult.DATA_TAG, null);
        return result;
    }

    protected AjaxResult forbidden(String message) {
        AjaxResult result = new AjaxResult(HttpStatus.FORBIDDEN, message);
        result.put(AjaxResult.DATA_TAG, null);
        return result;
    }

    protected <T> AjaxResult pageSuccess(List<T> list) {
        List<T> safeList = list != null ? list : Collections.<T>emptyList();
        return successWithData("查询成功", ApiPageResponse.fromList(new PageInfo<>(safeList)));
    }

    protected String resolveUploadErrorMessage(Exception exception, String fileLabel) {
        Throwable current = exception;
        while (current != null) {
            if (current instanceof MaxUploadSizeExceededException) {
                return fileLabel + "不能超过 " + resolveUploadLimitText();
            }
            current = current.getCause();
        }

        String message = StringUtils.defaultString(exception.getMessage()).toLowerCase();
        if (message.contains("maximum upload size exceeded")
            || message.contains("maxuploadsizeexceededexception")
            || message.contains("sizelimitexceededexception")
            || message.contains("upload.exceed.maxsize")
            || message.contains("allowed file maximum size")
            || message.contains("允许的文件最大大小")
            || message.contains("文件大小超出")
            || message.contains("超过最大大小")) {
            return fileLabel + "不能超过 " + resolveUploadLimitText();
        }

        return fileLabel + "上传失败，请换一张图片后重试";
    }

    protected String resolveUploadLimitText() {
        return StringUtils.isNotEmpty(maxUploadFileSize) ? maxUploadFileSize.trim().toUpperCase() : DEFAULT_UPLOAD_LIMIT_TEXT;
    }

    protected long resolveUploadLimitBytes() {
        try {
            return DataSize.parse(resolveUploadLimitText()).toBytes();
        } catch (Exception ignored) {
            return DEFAULT_UPLOAD_LIMIT_BYTES;
        }
    }

    protected File resolveUploadedProfileFile(String resourcePath) {
        if (StringUtils.isEmpty(resourcePath)) {
            return null;
        }
        String value = resourcePath.trim();
        String prefix = Constants.RESOURCE_PREFIX + "/";
        if (value.startsWith(prefix)) {
            return new File(RuoYiConfig.getProfile(), value.substring(prefix.length()));
        }
        if (value.startsWith(Constants.RESOURCE_PREFIX)) {
            String relativePath = value.substring(Constants.RESOURCE_PREFIX.length());
            while (relativePath.startsWith("/")) {
                relativePath = relativePath.substring(1);
            }
            return new File(RuoYiConfig.getProfile(), relativePath);
        }
        return new File(value);
    }

    protected void deleteUploadedProfileFile(String resourcePath) {
        File file = resolveUploadedProfileFile(resourcePath);
        if (file != null) {
            FileUtils.deleteFile(file.getAbsolutePath());
        }
    }
}
