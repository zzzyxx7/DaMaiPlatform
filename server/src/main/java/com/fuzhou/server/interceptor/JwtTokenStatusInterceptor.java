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

@Component
@Slf4j
public class JwtTokenStatusInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 非Controller方法（如静态资源）直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        String token = request.getHeader(jwtProperties.getUserTokenName());
        log.info("用户端登录状态校验：token={}", token);

        // 初始化登录状态相关变量
        boolean isLogin = false;
        Long userId = null;
        String loginMsg = "未登录";

        // 1. Token非空且不在黑名单
        if (StringUtils.hasText(token) && !redisUtil.isInBlacklist(token)) {
            try {
                // 2. 解析Token并校验
                Claims claims = jwtUtil.parseAccessToken(token);
                userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
                isLogin = true;
                loginMsg = "已登录";
                log.info("当前用户登录状态：{}，用户ID：{}", loginMsg, userId);
            } catch (RuntimeException e) {
                // Token无效/过期，仅记录日志，不拦截
                log.warn("Token校验失败：{}，原因：{}", token, e.getMessage());
                loginMsg = "Token无效：" + e.getMessage();
            }
        } else if (StringUtils.hasText(token) && redisUtil.isInBlacklist(token)) {
            // Token在黑名单，仅记录日志
            log.warn("Token已注销：{}", token);
            loginMsg = "Token已注销";
        }

        // 3. 绑定登录状态到Request（供Controller获取）
        request.setAttribute("isLogin", isLogin);
        request.setAttribute("loginUserId", userId);
        request.setAttribute("loginMsg", loginMsg);

        // 4. 核心：始终返回true，不拦截任何请求
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清除ThreadLocal（避免内存泄漏）
        BaseContext.removeCurrentId();
    }
}