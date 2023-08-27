
package com.ycg.daily.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ycg.daily.pojo.DailyInfo;
import org.springframework.web.bind.annotation.PathVariable;

public interface DailyInfoService extends IService<DailyInfo> {
    /**
     * 增加一片日记 todo 还未进行token拦截, 直接放行了
     *
     * @param dailyInfo
     * @return
     */
    void saveDaily(DailyInfo dailyInfo);

    /**
     * 查询所有人的公开的日记
     *
     * @param current 当前页
     * @param size    大小
     * @return page
     */
    Page<DailyInfo> publicPage(Long current,
                               Long size);

    /**
     * 查询用户自己的的所有日记
     * @param id
     * @return
     */
    Page<DailyInfo> getPageByUserId(Long current, Long size, String id);

    /**
     * 查询单个用户所有的公开日记
     * @param current 当前页
     * @param size 大小
     * @return page
     */
    Page<DailyInfo> publicPageByUserId(Long current, Long size, String id);
}