package com.ycg.daily.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ycg.daily.common.R;
import com.ycg.daily.pojo.DailyInfo;
import com.ycg.daily.service.DailyInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@Slf4j
@RestController
@RequestMapping("/daily")
public class DailyController {

    @Resource
    private DailyInfoService dailyInfoService;

    /**
     * 增加一篇日记 todo 还未进行token拦截, 直接放行了
     * @param dailyInfo
     * @return
     */
    @PostMapping("/add")
    public R<String> addDaily(@RequestBody DailyInfo dailyInfo) {
        dailyInfoService.saveDaily(dailyInfo);
        return R.success("添加成功");
    }


//    @GetMapping
//    public R<List<>>

    /**
     * 查询所有人的公开的日记
     * @param current 当前页
     * @param size 大小
     * @return page
     */
    @GetMapping("/public/{current}/{size}")
    public R<Page<DailyInfo>> publicPage(@PathVariable("current") Long current,
                                   @PathVariable("size") Long size) {
        return R.success(dailyInfoService.publicPage(current, size));
    }

    /**
     * 查询单个用户所有的公开日记
     * @param current 当前页
     * @param size 大小
     * @return page
     */
    @GetMapping("/public/{current}/{size}/{id}")
    public R<Page<DailyInfo>> publicPageByUserId(@PathVariable("current") Long current,
                                               @PathVariable("size") Long size,
                                               @PathVariable("id") String id) {
        if (StrUtil.isBlank(id)) {
            log.error("用户id为空");
            return R.error("用户id为空");
        }
        return R.success(dailyInfoService.publicPageByUserId(current, size, id));
    }


    /**
     * 查询用户 自己的(登录用户) 的所有日记
     * @param id
     * @return
     */
    @GetMapping("/personal/{current}/{size}/{id}")
    public R<Page<DailyInfo>> userPage(@PathVariable("current") Long current,
                                       @PathVariable("size") Long size,
                                       @PathVariable("id") String id) {
        if (StrUtil.isBlank(id)) {
            log.error("用户id为空");
            return R.error("用户id为空");
        }
        return R.success(dailyInfoService.getPageByUserId(current, size, id));
    }

    /**
     * 查询登录用户的一篇日记
     * @param id
     * @return
     */
    @GetMapping("/query/{id}")
    public R<DailyInfo> query(@PathVariable("id") Integer id) {
        return dailyInfoService.queryOne(id);
    }

    /**
     * 修改一篇日记
     * @param dailyInfo
     * @return
     */
    @PutMapping("/personal")
    public R<String> update(@RequestBody DailyInfo dailyInfo) {
        if (ObjectUtil.isEmpty(dailyInfo)) {
            return R.error("修改日记为空");
        }
        dailyInfoService.updateById(dailyInfo);
        return R.success("修改日记成功");
    }

}
