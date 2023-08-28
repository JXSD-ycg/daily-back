package com.ycg.daily.config;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ycg.daily.pojo.DailyInfo;
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


    /**
     * 缓存 首页 日记内容
     * @return
     */
    @Bean
    public Cache<String, Page<DailyInfo>> dailyCache() {
        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(10000)
                .build();
    }


}