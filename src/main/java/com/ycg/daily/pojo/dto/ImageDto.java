package com.ycg.daily.pojo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ImageDto implements Serializable {

    /**
     *  图片id
     */
    private Long id;
    /**
     * 图片url
     */
    private String url;
    /**
     * 图片类型
     */
    private Integer type;

    /**
     * 图片名称
     */
    private String name;
}
