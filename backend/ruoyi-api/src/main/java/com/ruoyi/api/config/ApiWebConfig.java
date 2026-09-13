package com.ruoyi.api.config;

import com.ruoyi.api.security.ApiTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 移动端 API Web 配置
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@Configuration
public class ApiWebConfig implements WebMvcConfigurer {

    @Autowired
    private ApiTokenInterceptor apiTokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiTokenInterceptor)
                // 拦截移动端 API 路径
                .addPathPatterns("/api/**")
                // 排除登录相关接口
                .excludePathPatterns(
                        "/api/auth/**",
                        "/api/common/**",
                        "/api/public/**"
                );
    }
}
