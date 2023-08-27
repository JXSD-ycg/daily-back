package com.ycg.daily.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CaffeineConfig {

    /**
     * 验证码缓存 包括 邮箱和图片验证码
     * @return
     */
    @Bean
    public Cache<String, String> codeCache() {
        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(10000)
                // 缓存有效期1小时
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build();
    }


}