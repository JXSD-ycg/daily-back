package com.ycg.daily.pojo.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ImageVO implements Serializable {
    /**
     * 图片id
     */
    private Long id;
    /**
     * 图片url
     */
    private String imageUrl;

}
