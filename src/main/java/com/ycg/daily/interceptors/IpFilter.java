package com.ycg.daily.interceptors;

import com.ycg.daily.common.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = "/*")
@Order(1)
@Slf4j
/**
 * 用于设置ip名单 防止部分ddos攻击
 */
public class IpFilter implements Filter {



    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 获取 当前请求的 ip
        String ip = request.getRemoteHost();
        log.info("请求ip => {}", ip);
        // 从缓存中获取 ip对应的请求限制次数

        // 如果没有 则新增

        // 如果有则减一

    }

    @Override
    public void destroy() {
        UserContext.clean();
    }


}
