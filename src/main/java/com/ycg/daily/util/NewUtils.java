package com.ycg.daily.util;

import com.ycg.daily.pojo.vo.NewVO;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class NewUtils {

    /**
     * 爬取澎湃新闻中的财经新闻, 返回map
     * @return Map key 是 文章标题, 值是文章内容 , 每日定时爬取, 放到缓存中
     */
    public static List<NewVO> getNews() {
        Connection connect = Jsoup.connect("https://www.thepaper.cn/finance");
        Document document = null;
        try {
            document = connect.get();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("爬取财经新闻数据异常");
        }
        assert document != null;
        Elements mdCard = document.getElementsByClass("mdCard");

        ArrayList<NewVO> news = new ArrayList<>();
        // 每天只爬取10个
        for (int i = 0; i < 10; i++) {
            Element element = mdCard.get(i);
            String title = element.getElementsByTag("h2").text();
            String content = element.getElementsByClass("index_desc__HjgHT").text();
            NewVO newVO = new NewVO();
            newVO.setTitle(title);
            newVO.setContent(content);
            news.add(newVO);
        }
        return news;
    }
}
