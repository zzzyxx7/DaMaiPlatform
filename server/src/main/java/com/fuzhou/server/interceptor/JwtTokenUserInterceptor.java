package com.fuzhou.server.interceptor;

import com.fuzhou.common.constant.JwtClaimsConstant;
import com.fuzhou.common.context.BaseContext;
import com.fuzhou.common.properties.JwtProperties;

import com.fuzhou.common.utils.JwtUtil;
import com.fuzhou.common.utils.RedisUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import java.io.IOException;

@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        String token = request.getHeader(jwtProperties.getUserTokenName());
        log.info("用户端JWT校验：token={}", token);

        if (!StringUtils.hasText(token)) {
            sendError(response, 40100, "未授权，请登录（Token为空）");
            return false;
        }
        if (redisUtil.isInBlacklist(token)) {
            response.setStatus(401);
            sendError(response, 40100, "未授权，Token已注销");
            return false;
        }
        try {
            // 调用解析方法
            Claims claims = jwtUtil.parseAccessToken(token);
            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            BaseContext.setCurrentId(userId);
            log.info("当前登录用户ID：{}", userId);
            return true;
        } catch (RuntimeException e) {
            // 根据异常消息中的错误标识判断具体情况
            String errorMsg = e.getMessage();
            if ("TOKEN_EXPIRED".equals(errorMsg)) {
                sendError(response, 40101, "Token已过期，请重新登录");
            } else if ("SIGNATURE_INVALID".equals(errorMsg)) {
                sendError(response, 40102, "Token无效（签名错误），请重新登录");
            } else if ("TOKEN_MALFORMED".equals(errorMsg)) {
                sendError(response, 40103, "Token格式错误，请检查");
            } else if ("TOKEN_UNSUPPORTED".equals(errorMsg)) {
                sendError(response, 40104, "不支持的Token类型");
            } else if ("TOKEN_EMPTY".equals(errorMsg)) {
                sendError(response, 40105, "Token为空或密钥错误");
            } else {
                sendError(response, 40199, "未授权，请登录");
            }
            return false;
        }
    }

    // 响应错误信息
    private void sendError(HttpServletResponse response, int code, String msg) throws IOException {
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":" + code + ",\"msg\":\"" + msg + "\"}");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        BaseContext.removeCurrentId(); // 清除ThreadLocal
    }
}