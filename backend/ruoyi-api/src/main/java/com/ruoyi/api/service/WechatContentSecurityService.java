package com.ruoyi.api.service;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.security.WxMaMsgSecCheckCheckRequest;
import cn.binarywang.wx.miniapp.bean.security.WxMaMsgSecCheckCheckResponse;
import com.ruoyi.weight.domain.WeightUser;
import com.ruoyi.weight.service.IWeightUserService;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * 微信小程序内容安全校验。
 */
@Service
public class WechatContentSecurityService {

    public static final int SCENE_PROFILE = 1;
    public static final int SCENE_COMMENT = 2;
    public static final int SCENE_FORUM = 3;
    public static final int SCENE_SOCIAL_LOG = 4;

    public static final String TEXT_RISK_MESSAGE = "内容含违规信息，请修改后再试";
    public static final String IMAGE_RISK_MESSAGE = "图片内容含违规信息，请换一张后重试";

    private static final Logger log = LoggerFactory.getLogger(WechatContentSecurityService.class);
    private static final String CHECK_UNAVAILABLE_MESSAGE = "内容安全校验暂不可用，请稍后再试";
    private static final String SUGGEST_PASS = "pass";
    private static final int WX_RISKY_CONTENT_CODE = 87014;

    @Autowired(required = false)
    private WxMaService wxMaService;

    @Autowired
    private IWeightUserService weightUserService;

    @Value("${wx.sec-check.enabled:true}")
    private boolean enabled;

    public String validateText(Long userId, String content, int scene, String source) {
        String text = StringUtils.trimToNull(content);
        if (!enabled || text == null) {
            return null;
        }

        if (wxMaService == null) {
            log.warn("微信内容安全校验未执行：WxMaService 未配置，source={}", source);
            return null;
        }

        String openid = resolveOpenid(userId);
        if (StringUtils.isBlank(openid)) {
            return validateByLegacyApi(text, source);
        }

        try {
            WxMaMsgSecCheckCheckRequest request = WxMaMsgSecCheckCheckRequest.builder()
                    .version("2")
                    .openid(openid)
                    .scene(scene)
                    .content(text)
                    .build();
            WxMaMsgSecCheckCheckResponse response = wxMaService.getSecCheckService().checkMessage(request);
            if (response == null) {
                log.warn("微信内容安全校验返回为空，source={}", source);
                return CHECK_UNAVAILABLE_MESSAGE;
            }
            if (response.getErrcode() != null && response.getErrcode() != 0) {
                log.warn("微信内容安全校验失败，source={}, errcode={}, errmsg={}",
                        source, response.getErrcode(), response.getErrmsg());
                return CHECK_UNAVAILABLE_MESSAGE;
            }
            WxMaMsgSecCheckCheckResponse.ResultBean result = response.getResult();
            if (result == null || SUGGEST_PASS.equalsIgnoreCase(StringUtils.defaultString(result.getSuggest()))) {
                return null;
            }
            log.info("微信内容安全校验拦截内容，source={}, suggest={}, label={}, traceId={}",
                    source, result.getSuggest(), result.getLabel(), response.getTraceId());
            return TEXT_RISK_MESSAGE;
        } catch (WxErrorException e) {
            if (isRiskyContent(e)) {
                log.info("微信内容安全校验拦截内容，source={}, error={}", source, e.getMessage());
                return TEXT_RISK_MESSAGE;
            }
            log.warn("微信内容安全校验接口异常，source={}, error={}", source, e.getMessage());
            return CHECK_UNAVAILABLE_MESSAGE;
        } catch (Exception e) {
            log.warn("微信内容安全校验执行异常，source={}", source, e);
            return CHECK_UNAVAILABLE_MESSAGE;
        }
    }

    public String validateTexts(Long userId, int scene, String source, String... contents) {
        if (contents == null || contents.length == 0) {
            return null;
        }
        for (String content : contents) {
            String message = validateText(userId, content, scene, source);
            if (message != null) {
                return message;
            }
        }
        return null;
    }

    public String validateImage(File imageFile, String source) {
        if (!enabled || imageFile == null) {
            return null;
        }

        if (wxMaService == null) {
            log.warn("微信图片安全校验未执行：WxMaService 未配置，source={}", source);
            return null;
        }

        if (!imageFile.isFile()) {
            log.warn("微信图片安全校验文件不存在，source={}, path={}", source, imageFile.getAbsolutePath());
            return CHECK_UNAVAILABLE_MESSAGE;
        }

        try {
            if (wxMaService.getSecCheckService().checkImage(imageFile)) {
                return null;
            }
            log.info("微信图片安全校验拦截图片，source={}, path={}", source, imageFile.getAbsolutePath());
            return IMAGE_RISK_MESSAGE;
        } catch (WxErrorException e) {
            if (isRiskyContent(e)) {
                log.info("微信图片安全校验拦截图片，source={}, error={}", source, e.getMessage());
                return IMAGE_RISK_MESSAGE;
            }
            log.warn("微信图片安全校验接口异常，source={}, error={}", source, e.getMessage());
            return CHECK_UNAVAILABLE_MESSAGE;
        } catch (Exception e) {
            log.warn("微信图片安全校验执行异常，source={}", source, e);
            return CHECK_UNAVAILABLE_MESSAGE;
        }
    }

    private String validateByLegacyApi(String content, String source) {
        try {
            if (wxMaService.getSecCheckService().checkMessage(content)) {
                return null;
            }
            log.info("微信内容安全旧版接口拦截内容，source={}", source);
            return TEXT_RISK_MESSAGE;
        } catch (WxErrorException e) {
            if (isRiskyContent(e)) {
                log.info("微信内容安全旧版接口拦截内容，source={}, error={}", source, e.getMessage());
                return TEXT_RISK_MESSAGE;
            }
            log.warn("微信内容安全旧版接口异常，source={}, error={}", source, e.getMessage());
            return CHECK_UNAVAILABLE_MESSAGE;
        }
    }

    private boolean isRiskyContent(WxErrorException exception) {
        return exception != null
                && exception.getError() != null
                && exception.getError().getErrorCode() == WX_RISKY_CONTENT_CODE;
    }

    private String resolveOpenid(Long userId) {
        if (userId == null) {
            return null;
        }
        WeightUser user = weightUserService.selectWeightUserByUserId(userId);
        return user != null ? StringUtils.trimToNull(user.getOpenid()) : null;
    }
}
