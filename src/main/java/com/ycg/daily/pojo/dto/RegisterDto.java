package com.ycg.daily.pojo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RegisterDto implements Serializable {

    /**
     * 注册邮箱
     */
    private String email;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮箱验证码
     */
    private String mailCode;


}
