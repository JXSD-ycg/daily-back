package com.ycg.daily.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * 邮箱发送工具类
 */
@Component
public class MailUtils {
    private static MailAccount account;
    static {
        account = new MailAccount();
        account.setHost("smtp.qq.com");
        account.setPort(465);
        account.setAuth(true);
        account.setFrom("3044254475@qq.com");
        //account.setUser("3044254475");
        account.setPass("ocsygwoipumoddij");
        account.setSslEnable(true);

    }

    /**
     * 发送注册邮箱
     * @param toMail
     * @param code
     */
    public static void sendRegisterMsg(String toMail, int code) {
        MailUtil.send(account, toMail, "注册随手记账号", "您好, 你正在登录随手记网站 \n验证码:\t\t\t\t" + code, false);
    }

    /**
     * 发送修改密码邮箱
     * @param toMail
     * @param code
     */
    public static void sendUpdateMsg(String toMail, int code) {
        MailUtil.send(account, toMail, "修改随手记密码", "您好, 你正在修改自己的随手记密码 \n验证码:\t\t\t\t" + code, false);
    }
    
}