package com.ruoyi.api.dto;

/**
 * 移动端上传配置响应
 */
public class ApiUploadConfigResponse {

    private Long maxFileSizeBytes;

    private String maxFileSizeText;

    public Long getMaxFileSizeBytes() {
        return maxFileSizeBytes;
    }

    public void setMaxFileSizeBytes(Long maxFileSizeBytes) {
        this.maxFileSizeBytes = maxFileSizeBytes;
    }

    public String getMaxFileSizeText() {
        return maxFileSizeText;
    }

    public void setMaxFileSizeText(String maxFileSizeText) {
        this.maxFileSizeText = maxFileSizeText;
    }
}
