package com.ycg.daily.pojo.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class NewVO implements Serializable {
    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章内容
     */
    private String content;

}
