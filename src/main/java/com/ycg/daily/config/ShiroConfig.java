package com.ycg.daily.config;


import com.ycg.daily.realm.MyCredentialsMatcher;
import com.ycg.daily.realm.MyRealm;
import com.ycg.daily.realm.TokenFilter;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {
    
    @Resource
    private MyRealm myRealm;
    
    @Resource
    private TokenFilter tokenFilter;
    
    @Bean  // 配置securityManager
    public DefaultWebSecurityManager defaultWebSecurityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        MyCredentialsMatcher matcher = new MyCredentialsMatcher();


        myRealm.setCredentialsMatcher(matcher); // 自定义加密
        myRealm.setAuthenticationCachingEnabled(true); // 认证缓存
        myRealm.setAuthorizationCachingEnabled(true); // 授权缓存
        // 5.创建认证对象,并设置认证策略(当有多个Realm时)
        ModularRealmAuthenticator modularRealmAuthenticator = new ModularRealmAuthenticator();
        /*
            三种认证策略
            1. AtLeastOneSuccessfulStrategy 只要有一个成功就成功 默认
            2. FirstSuccessfulStrategy 第一个成功,后续被忽略
            3. AllSuccessfulStrategy 全部成功才成功
         */
        modularRealmAuthenticator.setAuthenticationStrategy(new AtLeastOneSuccessfulStrategy());
        securityManager.setAuthenticator(modularRealmAuthenticator);
        securityManager.setRealm(myRealm);

        return securityManager;
    }

    
    @Bean("shiroFilterFactoryBean")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);
        // 指定过滤那些请求路径
        ShiroFilterChainDefinition filterChainDefinition = new DefaultShiroFilterChainDefinition();
        Map<String, String> filterMap = filterChainDefinition.getFilterChainMap();
        shiroFilter.setFilterChainDefinitionMap(filterMap);

        // 配置自己的过滤器
        Map<String, Filter> filters = new HashMap<>();
        filters.put("tokenFiler", tokenFilter);
        shiroFilter.setFilters(filters);

        return shiroFilter;
    }
    
}
