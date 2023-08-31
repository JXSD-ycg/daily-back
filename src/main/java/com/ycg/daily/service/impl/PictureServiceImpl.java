package com.ycg.daily.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.PathUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ycg.daily.common.R;
import com.ycg.daily.common.UserContext;
import com.ycg.daily.pojo.Picture;
import com.ycg.daily.service.PictureService;
import com.ycg.daily.mapper.PictureMapper;
import org.springframework.stereotype.Service;
import sun.net.util.URLUtil;

/**
* @author lenovo
* @description 针对表【picture(用户图片信息表 包括日记图片和用户头像 ,每个用户和图片(头像)都有唯一确认的md5的hash值)】的数据库操作Service实现
* @createDate 2023-08-30 11:17:52
*/
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureService{

    /**
     * 获取唯一图片
     * @param imageMd5 图片处理后的md5值
     * @param type     图片类型 0: 日记图片 1:头像
     * @return
     */
    @Override
    public Picture getUniquePicture(String imageMd5, Integer type) {
        LambdaQueryWrapper<Picture> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Picture::getUserId, UserContext.getCurrentId())
                .eq(Picture::getHash, imageMd5)
                .eq(Picture::getIsAvatar, type);
        return getOne(wrapper);
    }

    /**
     * 删除图片
     * @param imageUrl  图片url
     * @param type
     * @return
     */
    @Override
    public R<String> removePicture(Long id, String imageUrl, Integer type) {

        // 删除数据库 图片 信息
        removeById(id);
        // 删除本地存储的 图片 url格式为 http://127.0.0.1:8080/2023/8/131b7633973c4e7d88f44d9088ffebaa.png
        String fileUrl = imageUrl.replace("http://127.0.0.1:8080/", "D:/daily/");
        FileUtil.del(fileUrl);
        return R.success("删除成功");
    }


}




