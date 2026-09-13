package com.ruoyi.api.controller;

import com.ruoyi.api.dto.ApiUploadConfigResponse;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 移动端公共配置 API
 */
@RestController
@RequestMapping("/api/common")
public class ApiCommonController extends ApiBaseController {

    @GetMapping("/upload-config")
    public AjaxResult uploadConfig() {
        ApiUploadConfigResponse response = new ApiUploadConfigResponse();
        response.setMaxFileSizeBytes(resolveUploadLimitBytes());
        response.setMaxFileSizeText(resolveUploadLimitText());
        return successWithData("查询成功", response);
    }
}
