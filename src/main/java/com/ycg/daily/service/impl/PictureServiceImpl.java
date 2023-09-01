package com.ycg.daily.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.PathUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ycg.daily.common.R;
import com.ycg.daily.common.UserContext;
import com.ycg.daily.pojo.DailyInfo;
import com.ycg.daily.pojo.Picture;
import com.ycg.daily.pojo.dto.ImageDto;
import com.ycg.daily.service.PictureService;
import com.ycg.daily.mapper.PictureMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sun.net.util.URLUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lenovo
 * @description 针对表【picture(用户图片信息表 包括日记图片和用户头像 ,每个用户和图片(头像)都有唯一确认的md5的hash值)】的数据库操作Service实现
 * @createDate 2023-08-30 11:17:52
 */
@Slf4j
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
        implements PictureService {

    /**
     * 获取唯一图片
     *
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
     *
     * @param imageUrl 图片url
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

    /**
     * 调整图片返回格式 转成数组字符串  数组里面是一个ImageDto
     *
     * @param list 日记列表地
     * @return
     */
    @Override
    public void changeImageForm(List<DailyInfo> list) {
        if (ObjectUtil.isEmpty(list)) {
            log.warn("日记列表不能为空");
            return;
        }
        for (DailyInfo one : list) {
            ArrayList<ImageDto> imageDtos = null;
            // // 不为空, 说明日记有图片
            if (StrUtil.isNotEmpty(one.getImage())) {
                List<Long> imageIds = JSONUtil.toList(one.getImage(), Long.class);
                // 根据图片id查询图片
                List<Picture> pictures = listByIds(imageIds);
                imageDtos = getImageDtos(pictures);
            }
            // 后端返回的 image是一个列表, 里面是图片对象 id name url
            one.setImage(imageDtos == null ? "" : JSONUtil.toJsonStr(imageDtos));
        }

    }

    /**
     * 获取 图片列表 返回 ImageDto的list
     * @param pictures
     * @return
     */
    static ArrayList<ImageDto> getImageDtos(List<Picture> pictures) {
        ArrayList<ImageDto> imageDtos;
        imageDtos = new ArrayList<>();
        for (Picture picture : pictures) {
            ImageDto imageDto = new ImageDto();
            imageDto.setId(picture.getId());
            imageDto.setUrl(picture.getUrl());
            imageDto.setName(picture.getName());
            imageDtos.add(imageDto);
        }
        return imageDtos;
    }


}




