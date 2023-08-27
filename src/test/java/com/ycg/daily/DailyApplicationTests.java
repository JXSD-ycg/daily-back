package com.ycg.daily;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ycg.daily.common.R;
import com.ycg.daily.constants.RedisConstants;
import com.ycg.daily.pojo.DailyInfo;
import com.ycg.daily.service.DailyInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@SpringBootTest
class DailyApplicationTests {


    @Test
    void contextLoads() {
    }

    @Resource
    private DailyInfoService dailyInfoService;

    @Test
    void addDaily() {
        List<DailyInfo> list = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            DailyInfo dailyInfo = new DailyInfo();
            dailyInfo.setUsername("用户名称"+i);
            dailyInfo.setDailyTitle("日记标题" + i);
            dailyInfo.setContent("测试日记文本测试日" +i +
                    "测试日记文本测试日记文本测试日记文本测试日记文本" +i +
                    "测试日记文本测试日记文本测试日记文本测试日记文本" + i +
                    "记文本测试日记文本测试日记文本测试日记文本" + i);
            dailyInfo.setLikes(new Random().nextInt(1000));
            dailyInfo.setViews(new Random().nextInt(1000));
            dailyInfo.setBookTitle("default");
            dailyInfo.setUserId(7);
            dailyInfo.setIsPublic(1);
            dailyInfo.setImage("https://ts1.cn.mm.bing.net/th/id/R-C.eec02321ea106…nhrZdn2JEzUKk3lfW%2br0P70%3d&risl=&pid=ImgRaw&r=0");
            list.add(dailyInfo);
        }

        dailyInfoService.saveBatch(list);
    }

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Test
    void name() {
        // 同步数据库的日记信息 到redis
        //1. 查询数据库最新的 7条日记
        Page<DailyInfo> page = new Page<>(1, 7);
        LambdaQueryWrapper<DailyInfo> wrapper = new LambdaQueryWrapper<>();
        // 查询公开的日记
        wrapper.eq(DailyInfo::getIsPublic, 1);
        wrapper.orderByDesc(DailyInfo::getCreateTime);

        Page<DailyInfo> infoPage = dailyInfoService.page(page, wrapper);
        //2. 转换为字符串 存放到redis
        List<DailyInfo> records = infoPage.getRecords();
        stringRedisTemplate.opsForValue().set(RedisConstants.DAILY_INFO_KEY, JSONUtil.toJsonStr(records));

    }


    @Resource
    private RestTemplate restTemplate;

    @Test
    void sentence() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("app_id","tqiqmcqgptlsjkqy");
        headers.set("app_secret","2zoz5QfIblYnHMQiymtppMjCqyQzXahk");

        HttpEntity<String> http = new HttpEntity<>(headers);
        String url = " https://www.mxnzp.com/api/daily_word/recommend?count=1";

        ResponseEntity<R> entity = restTemplate.exchange(url, HttpMethod.GET, http, R.class, "");
        R body = entity.getBody();
        System.out.println(body);
    }

//    public static void main(String[] args) {
//        // 构建一个名字 url
//        LocalDateTime now = LocalDateTime.now();
//        int year = now.getYear();
//        int month = now.getMonth().getValue();
//
//        String suffix = ".png";
//
//        // 文件名称
//        String fileName = IdUtil.simpleUUID() + suffix;
//
//        String urlPrefix = "D:\\daily\\" +year + "\\" + month + "\\";
//        FileUtil.touch(urlPrefix+fileName);
//    }

    public static void main(String[] args) {

    }


}
