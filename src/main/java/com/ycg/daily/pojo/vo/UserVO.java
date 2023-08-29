package com.ycg.daily.pojo.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ycg.daily.enums.SexEnum;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserVO implements Serializable {
    /**
     * id
     */
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户的自我介绍
     */
    private String introduce;

    /**
     * 用户头像 url
     */
    private String image;

    /**
     * 用户性别 1:男, 0:女
     */
    private SexEnum sex;

    /**
     * 账号创建时间
     */
    private LocalDateTime createTime;

}
