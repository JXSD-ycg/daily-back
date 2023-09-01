package com.ycg.daily.service;

import com.ycg.daily.common.R;
import com.ycg.daily.pojo.DailyInfo;
import com.ycg.daily.pojo.Picture;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author lenovo
* @description 针对表【picture(用户图片信息表 包括日记图片和用户头像 ,每个用户和图片(头像)都有唯一确认的md5的hash值)】的数据库操作Service
* @createDate 2023-08-30 11:17:52
*/
public interface PictureService extends IService<Picture> {

    /**
     * 获取唯一图片
     * @param imageMd5  图片处理后的md5值
     * @param type      图片类型 0: 日记图片 1:头像
     * @return
     */
    Picture getUniquePicture(String imageMd5, Integer type);


    /**
     * 删除图片
     * @param imageUrl
     * @param type
     * @return
     */
    R<String> removePicture(Long id, String imageUrl, Integer type);

    /**
     * 调整图片返回格式 转成数组字符串  数组里面是一个ImageDto
     * @param list 日记列表地

     * @return
     */
    void changeImageForm(List<DailyInfo> list);


}
