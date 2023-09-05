package com.ycg.daily.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.*;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.ycg.daily.common.R;
import com.ycg.daily.common.UserContext;
import com.ycg.daily.constants.CaffeineConstants;
import com.ycg.daily.constants.ImageType;
import com.ycg.daily.pojo.DailyInfo;
import com.ycg.daily.pojo.Picture;
import com.ycg.daily.pojo.User;
import com.ycg.daily.pojo.vo.*;
import com.ycg.daily.service.CommonService;
import com.ycg.daily.service.DailyInfoService;
import com.ycg.daily.service.PictureService;
import com.ycg.daily.service.UserService;
import com.ycg.daily.util.HttpEntityUtils;
import com.ycg.daily.util.MailUtils;
import com.ycg.daily.util.NewUtils;
import lombok.extern.slf4j.Slf4j;
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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
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
     * @param type 邮箱类型  0:注册账号邮箱  1:修改密码邮箱
     */
    @Override
    public R<String> sentMail(String email, Short type) {

        // 参数校验
        if (StrUtil.isEmpty(email)) {
            return R.error("邮箱为空");
        }
        if (ObjectUtil.isNull(type) || (type != 1 && type != 0)) {
            return R.error("邮箱类型错误");
        }
        int code = getCodeByType(email,type);

        if (type == 0) {
            // 查询数据库 如果邮箱已经存在, 则不用继续注册
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getEmail, email);
            User one = userService.getOne(wrapper);
            if (ObjectUtil.isNotNull(one)) {
                return R.warning("用户已经注册");
            }
            // 存入缓存中
            codeCache.put(CaffeineConstants.REGISTER_MAIL + email, String.valueOf(code));
        }
        // type != 0 说明是来修改密码的
        codeCache.put(CaffeineConstants.UPDATE_MAIL + email, String.valueOf(code));

        return R.success("发送邮箱成功");
    }

    /**
     * 根据类型缓存对应的邮箱类型
     * @param email
     * @param type 邮箱类型  0:注册账号邮箱  1:修改密码邮箱
     * @return
     */
    private int getCodeByType(String email, Short type) {
        // 发送邮箱 生成4位的随机验证码
        int code = RandomUtil.randomInt(1000, 9999);
        log.info("邮箱验证码:" + code);
        if (type == 0) {
            MailUtils.sendRegisterMsg(email, code);
        } else if (type == 1) {
            MailUtils.sendUpdateMsg(email,code);
        }

        return code;
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
        codeCache.put(CaffeineConstants.PIC + id, code);
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
    public R<List<SentenceVO>> sentence() {
        HttpEntity<String> http = HttpEntityUtils.getHttpEntity();
        String url = HttpEntityUtils.getSentenceUrl();
        ResponseEntity<R> entity = restTemplate.exchange(url, HttpMethod.GET, http, R.class, "");
        R body = entity.getBody();
        String jsonStr = JSONUtil.toJsonStr(body.getData());
        // 使用toString 会将  : 变成 =
        // List<Sentence> sentenceList = JSONUtil.toList(body.getData().toString(), Sentence.class);
        List<SentenceVO> sentenceVOList = JSONUtil.toList(jsonStr, SentenceVO.class);

        return R.success(sentenceVOList);
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
    public R<ImageVO> uploadImage(MultipartFile files, Integer type) {
        // 检查图片类型
        if (ObjectUtil.isEmpty(type) && type != 1 && type != 0) {
            return R.error("图片类型错误");
        }

        // 后去文件路径  年/月/md5.png
        String filePath = getFilePath(files);
        String imageMd5 = getImageMd5(files);

        // 获取唯一的图片或头像
        Picture picture = pictureService.getUniquePicture(imageMd5, type);
        // 如果不为空, 说明已经有头像/图片了, 直接返回picture里面的url即可 这个是解决重复头像问题
        if (ObjectUtil.isNotNull(picture)) {
            ImageVO imageVO = new ImageVO();
            imageVO.setImageUrl(picture.getUrl());
            imageVO.setId(picture.getId());
            return R.success(imageVO);
        }

        String url = createImageUrl(files, filePath);
        // 成功上传 保存记录到 数据库
        Picture newPicture = new Picture();
        newPicture.setHash(imageMd5);
        newPicture.setIsAvatar(type);
        newPicture.setUserId(UserContext.getCurrentId());
        newPicture.setUrl(url);
        newPicture.setName(files.getName());
        pictureService.save(newPicture);

        // 如果是头像的上传, 新增头像之后, 要把之前的头像删除了
        if (type.equals(ImageType.AVATAR)) {
            // 删除之前的头像 ,包括数据库 和 本地文件
            delOldAvatar(newPicture);
        }

        ImageVO imageVO = new ImageVO();
        imageVO.setId(newPicture.getId());
        imageVO.setImageUrl(url);
        return R.success(imageVO);
    }

    /**
     * 删除用户 之前的头像
     *
     * @param newPicture 排除的 图片
     */
    private void delOldAvatar(Picture newPicture) {
        // 删除用户的之前头像
        LambdaQueryWrapper<Picture> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Picture::getUserId, UserContext.getCurrentId())
                .ne(Picture::getId, newPicture.getId())
                .eq(Picture::getIsAvatar, ImageType.AVATAR);
        // 删除 头像文件地址
        Picture one = pictureService.getOne(wrapper);
        if (ObjectUtil.isNotNull(one)) { // 第一次换头像可能为null
            String path = one.getUrl().replace("http://127.0.0.1:8080/", "D:/daily/");
            FileUtil.del(path);
            // 删除数据库数据
            pictureService.remove(wrapper);
        }
    }

    /**
     * 删除图片
     *
     * @param imageUrl
     * @param type
     * @return
     */
    @Override
    public R<String> deleteImage(Long id, String imageUrl, Integer type) {
        // 参数校验
        if (StrUtil.isEmpty(imageUrl) || !Objects.equals(type, ImageType.AVATAR) || !type.equals(ImageType.DAILY)) {
            R.error("参数错误");
        }
        return pictureService.removePicture(id, imageUrl, type);
    }

    @Resource
    private Cache<String, List<NewVO>> newsCache;
    /**
     * 获取财经新闻
     * @return
     */
    @Override
    public R<List<NewVO>> finance() {
        // 查询缓存
        List<NewVO> news = newsCache.get(CaffeineConstants.NEWS, (key) -> {
            log.warn("新闻缓存为命中");
            return null;
        });
        if (ObjectUtil.isNull(news)) {
            // 没有则爬取数据
            news = NewUtils.getNews();
            newsCache.put(CaffeineConstants.NEWS,news);
        }
        return R.success(news);
    }


    /**
     * 真正的创建图片url
     *
     * @param files
     * @param filePath
     * @return
     */
    private String createImageUrl(MultipartFile files, String filePath) {
        File touch = FileUtil.touch("D:/daily/" + filePath);
        try {
            FileUtil.writeBytes(files.getBytes(), touch);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "http://127.0.0.1:8080/" + filePath;
    }

    /**
     * 获取 图片流的 md5
     *
     * @param files
     * @return
     */
    private String getImageMd5(MultipartFile files) {
        String imageMd5 = null;
        try {
            imageMd5 = MD5.create().digestHex(files.getBytes());
        } catch (IOException e) {
            R.error("文件流异常");
        }
        return imageMd5;
    }

    /**
     * 获取文件路径 年/月/uuid.png  2023/8/uuid.png
     *
     * @param files
     * @return
     */
    private String getFilePath(MultipartFile files) {
        // 构建一个名字 url
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int month = now.getMonth().getValue();
        String suffix = "." + FileUtil.getSuffix(files.getOriginalFilename());
        // 文件名称
        return year + "/" + month + "/" + IdUtil.simpleUUID() + suffix;
    }

//    @Resource
//    private Cache<String, HolidayVO> holidayCache;

//    /**
//     * 获取日期信息, 节假日, 万年历, 节气等
//     * @return
//     */
//    public R<HolidayVO> holiday() {
//        // 先查询缓存
//        HolidayVO holidayVO;
//        holidayVO = holidayCache.get(CaffeineConstants.HOLIDAY_KEY, key -> {
//            log.warn("没有缓存日历信息");
//            return null;
//        });
//        if (ObjectUtil.isNull(holidayVO)) {
//            // 定时更新缓存汇总的日历信息
//            HttpEntity<String> http = HttpEntityUtils.getHttpEntity();
//            String url = HttpEntityUtils.getHolidayUrl();
//            ResponseEntity<R> entity = restTemplate.exchange(url, HttpMethod.GET, http, R.class, "");
//            R body = entity.getBody();
//            if (body.getCode() == 1) {
//                String jsonStr = JSONUtil.toJsonStr(body.getData());
//                holidayVO = JSONUtil.toBean(jsonStr, HolidayVO.class);
//                holidayCache.put(CaffeineConstants.HOLIDAY_KEY, holidayVO);
//            }
//            throw new RuntimeException("api错误");
//        }
//        return R.success(holidayVO);
//    }


}
