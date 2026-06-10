package com.example.ai_mental_helper.config;

import com.example.ai_mental_helper.common.JwtUtils;
import com.example.ai_mental_helper.exception.CustomException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 OPTIONS 请求（处理跨域常用）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 从请求头 Header 中提取 Authorization 凭证
        String token = request.getHeader("Authorization");

        if (token == null || token.isEmpty()) {
            throw new CustomException(401, "未检查到登录凭证，请先登录！");
        }

        // 解析 Token
        Claims claims = JwtUtils.parseToken(token);
        if (claims == null) {
            throw new CustomException(401, "登录凭证已失效或非法，请重新登录！");
        }

        // 选做扩展：可以将 userId 存入 request 域，方便后续 Controller 直接提取
        request.setAttribute("currentUserId", claims.get("userId"));
        request.setAttribute("currentUsername", claims.get("username"));

        return true; // 校验成功，放行
    }
}