package com.ruoyi.api.security;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * 移动端 API Token 拦截器
 * 
 * @author ruoyi
 * @date 2026-01-30
 */
@Component
public class ApiTokenInterceptor implements org.springframework.web.servlet.HandlerInterceptor {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    private static final List<String> OPTIONAL_AUTH_GET_PATTERNS = Arrays.asList(
            "/api/circle/list",
            "/api/circle/categories",
            "/api/circle/feed/recommended",
            "/api/circle/invite/resolve",
            "/api/circle/*",
            "/api/circle/*/feeds",
            "/api/circle/*/members",
            "/api/circle/*/ranking",
            "/api/feed/public/list",
            "/api/feed/*",
            "/api/feed/*/comments",
            "/api/user/*/profile",
            "/api/users/*/profile"
    );

    @Autowired
    private MiniAppTokenService miniAppTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 预检请求放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        boolean optionalAuth = isOptionalAuthRequest(request);
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            if (optionalAuth) {
                return true;
            }
            writeUnauthorizedResponse(response, "未授权，请先登录");
            return false;
        }

        try {
            String jwt = token.replace("Bearer ", "");
            Claims claims = miniAppTokenService.parseAccessToken(jwt);

            // 将用户信息存入 request 属性，方便后续使用
            request.setAttribute("userId", claims.get("userId"));
            request.setAttribute("openid", claims.get("openid"));
            request.setAttribute("claims", claims);

            return true;
        } catch (Exception e) {
            if (optionalAuth) {
                return true;
            }
            writeUnauthorizedResponse(response, "Token无效或已过期");
            return false;
        }
    }

    private boolean isOptionalAuthRequest(HttpServletRequest request) {
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            return false;
        }

        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }
        for (String pattern : OPTIONAL_AUTH_GET_PATTERNS) {
            if (PATH_MATCHER.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    private void writeUnauthorizedResponse(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"msg\":\"" + message + "\",\"data\":null}");
    }
}
