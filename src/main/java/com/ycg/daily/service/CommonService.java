package com.ycg.daily.service;

import com.ycg.daily.common.R;
import com.ycg.daily.pojo.vo.CodeMessageVo;
import com.ycg.daily.pojo.vo.ImageVO;
import com.ycg.daily.pojo.vo.Sentence;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface CommonService {
    /**
     * 发送邮箱服务
     */
    R<String> sentMail(String email);

    /**
     * 用户登录实现图形验证码功能
     *
     * @return
     */
    R<CodeMessageVo> generateCode(HttpServletResponse response);

    /**
     * 每日一句
     * @return
     */
    R<List<Sentence>> sentence();

    /**
     * 获取本站总体数据  用户数 日记数量等
     * @return
     */
    R<List<Integer>> getTotal();

    /**
     * 接收上传的图片 并保存
     * @param image 文件流
     * @param type  上传类型  1: 日记图片上传  0: 头像上传
     * @return
     */
    R<ImageVO> uploadImage(MultipartFile image, Integer type);


    /**
     * 删除图片
     * @param id   图片数据库id
     * @param imageUrl
     * @param type
     * @return
     */
    R<String> deleteImage(Long id, String imageUrl, Integer type);
}
