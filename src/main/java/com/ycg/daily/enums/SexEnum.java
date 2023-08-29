package com.ycg.daily.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum SexEnum {

    MALE(1,"男"),

    FEMALE(0,"女");

    SexEnum(int sex, String description) {
        this.sex = sex;
        this.description = description;
    }

    @EnumValue // 对应数据字段
    private final int sex;

    @JsonValue  // 返回给前端的值
    private final String description;


}
