package com.ycg.daily.controller;


import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.server.HttpServerResponse;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.ycg.daily.common.R;
import com.ycg.daily.constants.VerificationConstants;
import com.ycg.daily.pojo.dto.RegisterDto;
import com.ycg.daily.pojo.vo.CodeMessageVo;
import com.ycg.daily.pojo.vo.Sentence;
import com.ycg.daily.service.CommonService;
import com.ycg.daily.util.MailUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Resource
    private Cache<String, String> codeCache;

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
     *
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
    @RequestMapping("/sentence")
    public R<List<Sentence>> sentence() {
        return commonService.sentence();
    }

    /**
     * 获取本站总体数据  用户数 日记数量等
     * @return
     */
    @RequestMapping("/total")
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
    public R<String> uploadImage(@RequestParam MultipartFile files,
                                 @PathVariable("type") Integer type) {
        return commonService.uploadImage(files, type);
    }


}
