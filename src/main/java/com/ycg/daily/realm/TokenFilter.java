package com.ycg.daily.realm;

import cn.hutool.core.text.AntPathMatcher;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ycg.daily.common.UserContext;
import com.ycg.daily.constants.ExceptionConstants;
import com.ycg.daily.util.MyJwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.servlet.OncePerRequestFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@Component
public class TokenFilter extends OncePerRequestFilter {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /**
     * token 过滤器
     *
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        // 判断请求路径 如果是登录或者注册 则直接放行
        String requestURI = request.getRequestURI();
        log.info("本次请求的接口, {}", requestURI);

        // 定义拦截的的接口
        String[] publicUrls = new String[]{
//                "/daily/add",
//                "/user/deleteUser/**",
//                "/daily/edit"
        };

        boolean hit = check(publicUrls, requestURI);
        // 如果命中, 直接放行
        if (!hit) {
            log.info("本次请求不需要处理");
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }


        // 获取token
        String token = request.getHeader("token");

        // 判断是否有请求头
        if (StrUtil.isEmpty(token)) {
            throw new RuntimeException(ExceptionConstants.TOKEN_MISSING);
        }

        // 解析token
        Integer userId = MyJwtUtil.getId(token);
        if (ObjectUtil.isNull(userId)) {
            // token解析失败
            throw new RuntimeException(ExceptionConstants.TOKEN_RESOLVE_FAIL);
        }
        UserContext.setCurrentId(userId);
        // 放行
        filterChain.doFilter(servletRequest, servletResponse);
    }

    /**
     * 判断请求路径是否包含在 放行路径中
     *
     * @param publicUrls
     * @param requestURI
     * @return
     */
    private boolean check(String[] publicUrls, String requestURI) {
        for (String publicUrl : publicUrls) {
            if (PATH_MATCHER.match(publicUrl, requestURI)) {
                return true;
            }
        }
        return false;
    }
}
