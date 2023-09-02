package com.ycg.daily.util;

import com.ycg.daily.common.R;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HttpEntityUtils {
    /**
     * 里面配置了api的app_id 和 secret
     * @return
     */
    public static HttpEntity<String> getHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("app_id", "tqiqmcqgptlsjkqy");
        headers.set("app_secret", "2zoz5QfIblYnHMQiymtppMjCqyQzXahk");
        return new HttpEntity<>(headers);
    }

    /**
     * 返回每日一句url
     * @return
     */
    public static String getSentenceUrl() {
        return "https://www.mxnzp.com/api/daily_word/recommend?count=10";
    }

    /**
     * 返回日历url
     * @return
     */
    public static String getHolidayUrl() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String date = dateFormat.format(new Date());
        return "https://www.mxnzp.com/api/holiday/single/" + date + "?ignoreHoliday=false";
    }

    /**
     * 返回历史上的今天url
     * @return
     */
    public static String getHistoryUrl() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return " https://www.mxnzp.com/api/history/today?type=1";
    }
}
