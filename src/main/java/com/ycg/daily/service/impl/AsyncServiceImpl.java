package com.ycg.daily.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.benmanes.caffeine.cache.Cache;
import com.ycg.daily.constants.CaffeineConstants;
import com.ycg.daily.mapper.DailyInfoMapper;
import com.ycg.daily.pojo.DailyInfo;
import com.ycg.daily.service.AsyncService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 同步任务服务
 */
@Slf4j
@Service
public class AsyncServiceImpl implements AsyncService {

    @Resource
    private Cache<String,Page<DailyInfo>> dailyCache;

    @Resource
    private DailyInfoMapper dailyInfoMapper;

    /**
     * 同步数据库日记信息, 每次新增日记的时候调用
     */
    @Override
    @Async
    @PostConstruct
    public void dailyInfoAsync() {
        // 同步数据库的日记信息 到redis
        //1. 查询数据库最新的 7条日记
        Page<DailyInfo> page = new Page<>(1, 7);
        LambdaQueryWrapper<DailyInfo> wrapper = new LambdaQueryWrapper<>();
        // 查询公开的日记
        wrapper.eq(DailyInfo::getIsPublic, 1);
        wrapper.orderByDesc(DailyInfo::getCreateTime);

        Page<DailyInfo> infoPage = dailyInfoMapper.selectPage(page, wrapper);
        //2. 转换为字符串 存放到redis
        dailyCache.put(CaffeineConstants.DAILY_INFO_KEY,infoPage);

    }
}
