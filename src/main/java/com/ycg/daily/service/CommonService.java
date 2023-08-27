package com.ycg.daily.service;

import com.ycg.daily.common.R;
import com.ycg.daily.pojo.vo.CodeMessageVo;
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
     * 解说上传的图片 并保存
     * @param image 文件流
     * @return
     */
    R<String> uploadImage(MultipartFile image) ;
}
