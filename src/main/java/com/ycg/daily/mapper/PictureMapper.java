package com.ycg.daily.mapper;

import com.ycg.daily.pojo.Picture;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author lenovo
* @description 针对表【picture(用户图片信息表 包括日记图片和用户头像 ,每个用户和图片(头像)都有唯一确认的md5的hash值)】的数据库操作Mapper
* @createDate 2023-08-30 11:17:52
* @Entity com.ycg.daily.pojo.Picture
*/
public interface PictureMapper extends BaseMapper<Picture> {

}




