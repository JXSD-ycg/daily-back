package com.ycg.daily.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.benmanes.caffeine.cache.Cache;
import com.ycg.daily.common.R;
import com.ycg.daily.constants.CaffeineConstants;
import com.ycg.daily.mapper.DailyInfoMapper;
import com.ycg.daily.pojo.DailyInfo;
import com.ycg.daily.pojo.Picture;
import com.ycg.daily.pojo.dto.ImageDto;
import com.ycg.daily.pojo.vo.HolidayVO;
import com.ycg.daily.service.AsyncService;

import com.ycg.daily.service.DailyInfoService;
import com.ycg.daily.service.PictureService;
import com.ycg.daily.util.HttpEntityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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

    @Resource
    private PictureService pictureService;

    /**
     * 缓存同步数据库日记信息, 每次新增日记的时候 和项目重启的时候调用 不能异步调用 有延迟
     */
    @Override
    @PostConstruct
    public void dailyInfoAsync() {
        //1. 查询数据库最新的 7条日记
        Page<DailyInfo> page = new Page<>(1, 7);
        LambdaQueryWrapper<DailyInfo> wrapper = new LambdaQueryWrapper<>();
        // 查询公开的日记
        wrapper.eq(DailyInfo::getIsPublic, 1);
        wrapper.orderByDesc(DailyInfo::getCreateTime);

        Page<DailyInfo> infoPage = dailyInfoMapper.selectPage(page, wrapper);
        // 修改一下返回的图片参数格式
        pictureService.changeImageForm(infoPage.getRecords());
        //2. 转换为字符串 存放到缓存
        dailyCache.invalidate(CaffeineConstants.DAILY_INFO_KEY);
        dailyCache.put(CaffeineConstants.DAILY_INFO_KEY,infoPage);
    }

    /**
     * 更新 日记观看数
     * @param one
     */
    public void updateViews(DailyInfo one) {
        //将日记的view加1
        LambdaUpdateWrapper<DailyInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(DailyInfo::getId, one.getId())
                .set(DailyInfo::getViews, one.getViews() + 1);
        dailyInfoMapper.update(one,updateWrapper);
        dailyInfoAsync();
    }

}
