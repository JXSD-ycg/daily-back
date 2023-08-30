package com.ycg.daily.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ycg.daily.pojo.Picture;
import com.ycg.daily.service.PictureService;
import com.ycg.daily.mapper.PictureMapper;
import org.springframework.stereotype.Service;

/**
* @author lenovo
* @description 针对表【picture(用户图片信息表 包括日记图片和用户头像 ,每个用户和图片(头像)都有唯一确认的md5的hash值)】的数据库操作Service实现
* @createDate 2023-08-30 11:17:52
*/
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureService{

}




