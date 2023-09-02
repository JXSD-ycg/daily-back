
package com.ycg.daily.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.benmanes.caffeine.cache.Cache;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;
import com.ycg.daily.common.R;
import com.ycg.daily.constants.CaffeineConstants;
import com.ycg.daily.mapper.DailyInfoMapper;
import com.ycg.daily.pojo.DailyInfo;
import com.ycg.daily.pojo.Picture;
import com.ycg.daily.pojo.dto.ImageDto;
import com.ycg.daily.service.AsyncService;
import com.ycg.daily.service.DailyInfoService;

import javax.annotation.Resource;

import com.ycg.daily.service.PictureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.function.Function;

@Slf4j
@Service
public class DailyInfoServiceImpl extends ServiceImpl<DailyInfoMapper, DailyInfo> implements DailyInfoService {

    @Resource
    private AsyncService asyncService;


    public void saveDaily(DailyInfo dailyInfo) {
        this.save(dailyInfo);
        this.asyncService.dailyInfoAsync();
    }


    @Resource
    private Cache<String, Page<DailyInfo>> dailyCache;

    /**
     * 获取 首页文章内容
     * @param current 当前页
     * @param size    大小
     * @return
     */
    public  R<Page<DailyInfo>> publicPage(Long current, Long size) {
        Page<DailyInfo> page = this.getInfoPage(current, size);
        if (page.getCurrent() == 1L && page.getSize() == 7L) {
            Page<DailyInfo> dailyInfoPage = dailyCache.get(CaffeineConstants.DAILY_INFO_KEY, s -> {
                log.error("没有从caffeine中获取到日记数据");
                return null;
            });
            if (!ObjectUtil.isEmpty(dailyInfoPage)) {
                log.info("读取缓存日记");
                return R.success(dailyInfoPage);
            }
        }

        LambdaQueryWrapper<DailyInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DailyInfo::getIsPublic, 1);
        wrapper.orderByDesc(DailyInfo::getCreateTime, DailyInfo::getCreateTime);
        page = this.page(page, wrapper);

        // 修改一下 返回的 图片格式
        pictureService.changeImageForm(page.getRecords());
        return R.success(page);
    }

    public R<Page<DailyInfo>> getPageByUserId(Long current, Long size, String id) {
        Page<DailyInfo> page = this.getInfoPage(current, size);
        LambdaQueryWrapper<DailyInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DailyInfo::getUserId, id);
        wrapper.orderByDesc(DailyInfo::getCreateTime);
        page = this.page(page, wrapper);
        // 修改一下 返回的 图片格式
        pictureService.changeImageForm(page.getRecords());
        return R.success(page);
    }

    public  R<Page<DailyInfo>> publicPageByUserId(Long current, Long size, String id) {
        Page<DailyInfo> infoPage = this.getInfoPage(current, size);
        LambdaQueryWrapper<DailyInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DailyInfo::getUserId, id);
        wrapper.eq(DailyInfo::getIsPublic, 1);
        wrapper.orderByDesc(DailyInfo::getCreateTime);
        Page<DailyInfo> page = this.page(infoPage, wrapper);
        // 修改一下 返回的 图片格式
        pictureService.changeImageForm(page.getRecords());

        return R.success(page);
    }

    @Resource
    private PictureService pictureService;

    /**
     * 查询一篇日记
     *
     * @param id
     * @return
     */
    @Transactional
    @Override
    public R<DailyInfo> queryOne(Integer id) {
        if (ObjectUtil.isEmpty(id)) {
            return R.error("日记id为空");
        }
        LambdaQueryWrapper<DailyInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DailyInfo::getId, id);
        DailyInfo one = getOne(wrapper);
        // 异步 将日记观看数量 加 1
        asyncService.updateViews(one);

        // 修改一下 返回的 图片格式
        pictureService.changeImageForm(Collections.singletonList(one));

        return R.success(one);
    }

    /**
     * 更新一篇日记
     *
     * @param dailyInfo
     * @return
     */
    @Override
    public R<String> updateDailyById(DailyInfo dailyInfo) {
        updateById(dailyInfo);
        // 同步数据
        asyncService.dailyInfoAsync();
        return R.success("修改成功");
    }


    private Page<DailyInfo> getInfoPage(Long current, Long size) {
        if (ObjectUtil.isEmpty(current) || current <= 0L) {
            current = 1L;
        }

        if (ObjectUtil.isEmpty(size)) {
            size = 7L;
        }

        return new Page<>(current, size);
    }
}
