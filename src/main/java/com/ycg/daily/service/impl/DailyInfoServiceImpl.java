
package com.ycg.daily.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ycg.daily.mapper.DailyInfoMapper;
import com.ycg.daily.pojo.DailyInfo;
import com.ycg.daily.service.AsyncService;
import com.ycg.daily.service.DailyInfoService;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DailyInfoServiceImpl extends ServiceImpl<DailyInfoMapper, DailyInfo> implements DailyInfoService {

    @Resource
    private AsyncService asyncService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public DailyInfoServiceImpl() {
    }

    public void saveDaily(DailyInfo dailyInfo) {
        this.save(dailyInfo);
        this.asyncService.dailyInfoAsync();
    }

    public Page publicPage(Long current, Long size) {
        Page<DailyInfo> page = this.getInfoPage(current, size);
        if (page.getCurrent() == 1L && page.getSize() == 7L) {
            String pageInfo = (String)this.stringRedisTemplate.opsForValue().get("daily_info");
            if (!StrUtil.isEmpty(pageInfo)) {
                log.info("读取缓存日记");
                return JSONUtil.toBean(pageInfo, Page.class);
            }
        }

        LambdaQueryWrapper<DailyInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DailyInfo::getIsPublic, 1);
        wrapper.orderByDesc(DailyInfo::getCreateTime, DailyInfo::getCreateTime);

        return this.page(page, wrapper);
    }

    public Page<DailyInfo> getPageByUserId(Long current, Long size, String id) {
        Page<DailyInfo> page = this.getInfoPage(current, size);
        LambdaQueryWrapper<DailyInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DailyInfo::getUserId, id);
        return this.page(page, wrapper);
    }

    public Page<DailyInfo> publicPageByUserId(Long current, Long size, String id) {
        Page<DailyInfo> infoPage = this.getInfoPage(current, size);
        LambdaQueryWrapper<DailyInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DailyInfo::getUserId, id);
        wrapper.eq(DailyInfo::getIsPublic, 1);
        return this.page(infoPage, wrapper);
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
