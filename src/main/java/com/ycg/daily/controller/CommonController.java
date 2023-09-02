package com.ycg.daily.controller;


import com.github.benmanes.caffeine.cache.Cache;
import com.ycg.daily.common.R;
import com.ycg.daily.pojo.dto.ImageDto;
import com.ycg.daily.pojo.vo.*;
import com.ycg.daily.service.CommonService;
import com.ycg.daily.util.MailUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Resource
    private MailUtils mailUtils;

    @Resource
    private CommonService commonService;

    /**
     * 用户注册实现邮箱验证码功能
     *
     * @return
     */
    @GetMapping("/sentMail/{mail}")
    public R<String> sentMail(@PathVariable("mail") String email) {
        return commonService.sentMail(email);
    }

    /**
     * 用户登录实现图形验证码功能
     * @return
     */
    @GetMapping("/generateCode")
    public R<CodeMessageVo> generateCode(HttpServletResponse response) {
        return commonService.generateCode(response);
    }

    /**
     * 每日一句
     * @return
     */
    @GetMapping("/sentence")
    public R<List<SentenceVO>> sentence() {
        return commonService.sentence();
    }

    /**
     * 获取财经新闻
     * @return
     */
    @GetMapping("/finance")
    public R<List<NewVO>> finance() {
        return commonService.finance();
    }

//    /**
//     * 获取日期信息, 节假日, 万年历, 节气等
//     * @return
//     */
//    @GetMapping("/holiday")
//    public R<HolidayVO> holiday() {
//        return commonService.holiday();
//    }


    /**
     * 获取本站总体数据  用户数 日记数量等
     * @return
     */
    @GetMapping("/total")
    public R<List<Integer>> getTotal() {
        return commonService.getTotal();
    }


    /**
     * 接受上传的图片并保存
     * @param files 参数要和前面传过来名字一样
     * @param type  上传类型  1: 日记图片上传  0: 头像上传
     * @return
     */
    @PostMapping("/upload/{type}")
    public R<ImageVO> uploadImage(@RequestParam MultipartFile files,
                                  @PathVariable("type") Integer type) {
        return commonService.uploadImage(files, type);
    }

    /**
     * 删除图片功能
     * @param
     * @return
     */
    @DeleteMapping("/delete")
    public R<String> deleteImage(@RequestBody ImageDto dto) {

        return commonService.deleteImage(dto.getId(), dto.getUrl(), dto.getType());
    }


}
