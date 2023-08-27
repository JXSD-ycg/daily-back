package com.ycg.daily.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ycg.daily.pojo.DailyInfo;
import com.ycg.daily.service.DailyInfoService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.annotation.security.RunAs;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
class DailyControllerTest {

    @Resource
    private DailyInfoService dailyInfoService;

    public static void main(String[] args) throws IOException {
//        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 100);
//        String code = lineCaptcha.getCode();
//        System.out.println("验证码为" + code);
//        System.out.println(lineCaptcha.getImageBase64Data());

        String url = "/a/b/123";
        String[] split = url.split("/");
        for (String s : split) {
            System.out.println(s);
        }
    }

    @Test
    void page() {
        Page<DailyInfo> page = new Page<>(0,10);
        LambdaQueryWrapper<DailyInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DailyInfo::getIsPublic,1);
        Page<DailyInfo> infoPage = dailyInfoService.page(page, wrapper);
        for (DailyInfo record : infoPage.getRecords()) {
            System.out.println(record + "\n");
        }
    }
}