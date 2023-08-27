package com.ycg.daily.realm;

import cn.hutool.cache.CacheUtil;
import cn.hutool.crypto.digest.MD5;
import com.ycg.daily.common.UserContext;
import com.ycg.daily.constants.ExceptionConstants;
import com.ycg.daily.pojo.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;

public class MyCredentialsMatcher implements CredentialsMatcher {
    /**
     * token 用户输入的 token 我们的是usernamePassword
     * info 经过realm查询过后的
     */
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        // 查到数据后判断密码是否正确
        User user = (User) info.getCredentials();
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        String salt = user.getSalt();
        String dbPassword = user.getPassword();
        if (!MD5.create().digestHex(String.valueOf(usernamePasswordToken.getPassword()) + salt).equals(dbPassword)) {
            throw new RuntimeException(ExceptionConstants.PASSWORD_ERROR);
        }

        // 验证成功 将用户id存入 线程
        UserContext.setCurrentId(user.getId());

        return true;
    }
}
