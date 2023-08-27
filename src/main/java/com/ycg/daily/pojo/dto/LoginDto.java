package com.ycg.daily.pojo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginDto implements Serializable {

    /**
     * 登录邮箱
     */
    private String email;

    /**
     * 登录密码
     */
    private String password;

    /**
     * 图片验证码
     */
    private String picCode;

    /**
     * 图片验证码 唯一标识
     */
    private long codeId;

}
