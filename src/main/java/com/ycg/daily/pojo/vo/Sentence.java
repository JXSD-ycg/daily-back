package com.ycg.daily.pojo.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class Sentence implements Serializable {
    private String content;
    private String author;
}
