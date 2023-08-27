package com.ycg.daily.pojo.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CodeMessageVo implements Serializable {

    /**
     * 验证码的唯一标识  当前时间毫秒值
     */
    private long id;

    /**
     * 验证码数据 编码为base64
     */
    private String codeData;

}
