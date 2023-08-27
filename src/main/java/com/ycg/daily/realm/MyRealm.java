package com.ycg.daily.realm;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ycg.daily.common.UserContext;
import com.ycg.daily.constants.ExceptionConstants;
import com.ycg.daily.pojo.User;
import com.ycg.daily.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;


import static com.ycg.daily.constants.ExceptionConstants.SKIP_TO_AUTHORIZED;

@Slf4j
@Component
public class MyRealm extends AuthorizingRealm {


    @Resource
    private UserService userService;

    /**
     * 查询用户权限信息 目前只有管理员和普通用户
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        Integer currentId = UserContext.getCurrentId();
        // 如果用户未登录就想要执行权限操作, 直接抛出异常

        if (ObjectUtil.isEmpty(currentId)) {
            throw new RuntimeException(ExceptionConstants.SKIP_TO_AUTHORIZED);
        }

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        User user = userService.getById(currentId);
        if (user.getIsAdmin() == 1) {
            info.addRole("admin");
        }

        return info;
    }

    /**
     * 这里的username就是email, 也就是 Principal
     *
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) authenticationToken;
        String username = usernamePasswordToken.getUsername();
        String password = String.valueOf(usernamePasswordToken.getPassword());

        User user = getUser(username);

        // 把校验成功的的用户名和用户信息
        return new SimpleAuthenticationInfo(
                user.getEmail(),
                user, // 加密后的密码
                getName()
        );
    }


    /**
     * 查询数据库  是否禁用等
     *
     * @param username
     * @return
     */
    public User getUser(String username) {
        // 前面已经经过了 参数校验, 账号和密码都是有值的
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, username);
        User user = userService.getOne(wrapper);

        if (ObjectUtil.isNull(user)) {
            log.error("用户不存在");
            throw new RuntimeException(ExceptionConstants.USER_NOT_EXIST);
        }
        // 判断是否禁用状态
        if (user.getStatus() == 0) {
            log.error("用户被禁用");
            throw new RuntimeException(ExceptionConstants.USER_IS_DISABLE);
        }
        return user;

    }


}
