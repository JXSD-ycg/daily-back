package com.ycg.daily.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.*;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.sun.org.apache.bcel.internal.util.ClassLoader;
import com.ycg.daily.common.R;
import com.ycg.daily.common.UserContext;
import com.ycg.daily.constants.UploadType;
import com.ycg.daily.constants.VerificationConstants;
import com.ycg.daily.pojo.DailyInfo;
import com.ycg.daily.pojo.Picture;
import com.ycg.daily.pojo.User;
import com.ycg.daily.pojo.vo.CodeMessageVo;
import com.ycg.daily.pojo.vo.Sentence;
import com.ycg.daily.service.CommonService;
import com.ycg.daily.service.DailyInfoService;
import com.ycg.daily.service.PictureService;
import com.ycg.daily.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.data.annotation.Version;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

@Slf4j
@Service
public class CommonServiceImpl implements CommonService {

    @Resource
    private Cache<String, String> codeCache;

    /**
     * 发送邮箱服务
     *
     * @param email
     */
    @Override
    public R<String> sentMail(String email) {

        // 参数校验
        if (StrUtil.isEmpty(email)) {
            return R.error("邮箱为空");
        }
        // 发送邮箱 生产4位的随机验证码
        int code = RandomUtil.randomInt(1000, 9999);
        log.info("邮箱验证码:" + code);

        // 存入缓存中
        codeCache.put(VerificationConstants.MAIL + email, String.valueOf(code));

        return R.success("发送邮箱成功");
    }

    /**
     * 用户登录实现图形验证码功能
     *
     * @param response
     * @return
     */
    @Override
    public R<CodeMessageVo> generateCode(HttpServletResponse response) {
        //定义图形验证码的长和宽
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 100);
        String code = lineCaptcha.getCode();
        //输出code
        log.info("图片验证码 : {}", code);
        // 随机生成一个uuid 作为验证码的key
//        String key = IdUtil.simpleUUID();
//        Cookie cookie = new Cookie("pic_code", key);
//        response.addCookie(cookie);
//        try {
//            response.getOutputStream().write(lineCaptcha.getImageBytes());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        long id = System.currentTimeMillis();
        // 加入缓存中
        codeCache.put(VerificationConstants.PIC + id, code);
        response.setContentType("application/json; charset=UTF-8");
        // 返回base64编码数据
        // String imageBase64 = lineCaptcha.getImageBase64Data();
        // 返回字节数组

        CodeMessageVo codeMessage = new CodeMessageVo();

        codeMessage.setId(id);
        codeMessage.setCodeData(lineCaptcha.getImageBase64Data());

        return R.success(codeMessage);
    }

    @Resource
    private RestTemplate restTemplate;

    /**
     * 每日一句
     *
     * @return
     */
    @Override
    public R<List<Sentence>> sentence() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("app_id", "tqiqmcqgptlsjkqy");
        headers.set("app_secret", "2zoz5QfIblYnHMQiymtppMjCqyQzXahk");
        HttpEntity<String> http = new HttpEntity<>(headers);
        String url = "https://www.mxnzp.com/api/daily_word/recommend?count=10";
        ResponseEntity<R> entity = restTemplate.exchange(url, HttpMethod.GET, http, R.class, "");
        R body = entity.getBody();
        String jsonStr = JSONUtil.toJsonStr(body.getData());
        // 使用toString 会将  : 变成 =
        // List<Sentence> sentenceList = JSONUtil.toList(body.getData().toString(), Sentence.class);
        List<Sentence> sentenceList = JSONUtil.toList(jsonStr, Sentence.class);

        return R.success(sentenceList);
    }

    @Resource
    private UserService userService;

    @Resource
    private DailyInfoService dailyInfoService;

    /**
     * 获取本站总体数据  用户数 日记数量等
     *
     * @return
     */
    @Override
    public R<List<Integer>> getTotal() {
        // 调用userService和 dailyService
        int userCount = userService.count(new LambdaQueryWrapper<User>().eq(User::getIsDelete, 0));
        LambdaQueryWrapper<DailyInfo> publicWrapper = new LambdaQueryWrapper<>();
        publicWrapper.eq(DailyInfo::getIsDelete, 0);
        publicWrapper.eq(DailyInfo::getIsPublic, 1);
        // 查询公开日记
        int publicDailyCount = dailyInfoService.count(publicWrapper);

        LambdaQueryWrapper<DailyInfo> personalWrapper = new LambdaQueryWrapper<>();
        personalWrapper.eq(DailyInfo::getIsDelete, 0);
        personalWrapper.eq(DailyInfo::getIsPublic, 0);

        // 查询私有日记
        int personDailyCount = dailyInfoService.count(personalWrapper);

        ArrayList<Integer> list = new ArrayList<>();
        list.add(userCount);
        list.add(publicDailyCount);
        list.add(personDailyCount);
        return R.success(list);
    }

    @Resource
    private PictureService pictureService;

    /**
     * 接受要上传的图片 并保存 保存格式  d:/dailyImage/2023/08/文件名
     *
     * @param type  上传类型  0: 日记图片上传  1: 头像上传
     * @param files 文件流
     * @return
     */
    @Transactional
    @Override
    public R<String> uploadImage(MultipartFile files, Integer type) {
        // 构建一个名字 url
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int month = now.getMonth().getValue();
        String suffix = "." + FileUtil.getSuffix(files.getOriginalFilename());

        // 文件名称
        String fileName = IdUtil.simpleUUID() + suffix;

        String urlPrefix = "D:/daily/";
        String urlSuffix = year + "/" + month + "/";
        String imageMd5 = null;
        try {
            imageMd5 = MD5.create().digestHex(files.getBytes());
        } catch (IOException e) {
            R.error("文件流异常");
        }
        // 图片重复问题
        // 判断是否是头像上传 头像对一个用户来说是唯一的, 要删除之前的头像
        if ((Objects.equals(type, UploadType.AVATAR))) {
            // 查询数据库 获取唯一 MD5
            LambdaQueryWrapper<Picture> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Picture::getUserId, UserContext.getCurrentId())
                    .eq(Picture::getHash, imageMd5)
                    .eq(Picture::getIsAvatar, type);
            Picture picture = pictureService.getOne(wrapper);
            // 如果不为空, 说明已经有头像了, 直接返回picture里面的url即可
            if (ObjectUtil.isNotNull(picture)) {
                return R.success(picture.getUrl());
            }
        }


        File touch = FileUtil.touch(urlPrefix + urlSuffix + fileName);
        try {
            FileUtil.writeBytes(files.getBytes(), touch);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String url = "http://127.0.0.1:8080/" + urlSuffix + fileName;
        // 成功上传 保存记录到 数据库
        Picture newPicture = new Picture();
        newPicture.setHash(imageMd5);
        newPicture.setIsAvatar(type);
        newPicture.setUserId(UserContext.getCurrentId());
        newPicture.setUrl(url);
        pictureService.save(newPicture);

        return R.success(url);
    }
}
