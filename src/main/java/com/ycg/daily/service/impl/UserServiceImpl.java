package com.ycg.daily.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.benmanes.caffeine.cache.Cache;
import com.ycg.daily.common.R;
import com.ycg.daily.common.UserContext;
import com.ycg.daily.constants.CaffeineConstants;
import com.ycg.daily.pojo.User;
import com.ycg.daily.pojo.dto.LoginDto;
import com.ycg.daily.pojo.dto.RegisterDto;
import com.ycg.daily.pojo.vo.LoginVO;
import com.ycg.daily.pojo.vo.UserVO;
import com.ycg.daily.service.UserService;
import com.ycg.daily.mapper.UserMapper;
import com.ycg.daily.util.MyJwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author lenovo
 * @description 针对表【daily_user(用户账号的基本信息)】的数据库操作Service实现
 * @createDate 2023-08-21 11:47:19
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private Cache<String, String> codeCache;

    /**
     *
     * 用户修改密码接口
     * @param registerDto
     * @return
    */
    @Override
    public R<String> updatePass(RegisterDto registerDto) {
        // 参数校验 验证码是否正确
        String errorMsg = checkRegisterDto(registerDto, 1);
        if (StrUtil.isNotEmpty(errorMsg)) {
            return R.error(errorMsg);
        }

        // 验证通过 生成盐信息 并加密密码
        String salt = getSalt();
        String password = getMd5PassWithSalt(registerDto, salt);
        // 查询数据库原来的账号
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, registerDto.getEmail());
        User user = getOne(wrapper);
        if (ObjectUtil.isNull(user)) {
            return R.error("账号不存在");
        }
        user.setSalt(salt);
        user.setPassword(password);
        updateById(user);
        //  清理邮箱验证码的缓存
        codeCache.invalidate(CaffeineConstants.UPDATE_MAIL+registerDto.getEmail());

        // 登录成功, 生成token返回
        return R.success("注册成功");
    }

    /**
     * 根据原始密码和盐值来加密密码
     * @param registerDto
     * @param salt
     * @return
     */
    private String getMd5PassWithSalt(RegisterDto registerDto, String salt) {
        return MD5.create().digestHex(registerDto.getPassword() + salt);
    }

    /**
     * 修改用户
     * 返回
     * @param user
     * @return
     */
    @Override
    public R<UserVO> updateUser(User user) {
        if (ObjectUtil.isEmpty(user)){
            return R.error("user对象不为空");
        }
        updateById(user);
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return R.success(userVO);
    }

    /**
     * 用户登录, 判断token
     * @param loginDto 登录参数
     * @return R
     */
    @Override
    public R<LoginVO> login(LoginDto loginDto, HttpServletRequest request) {
        // 参数校验
        if (ObjectUtil.isNull(loginDto)) {
            return R.error("登录信息为空");
        }
        if (StrUtil.isEmpty(loginDto.getPicCode())) {
            log.error("验证码为空");
            return R.error("验证码为空");
        }
        if (ObjectUtil.isEmpty(loginDto.getCodeId())) {
            log.error("验证码凭证为空");
            return R.error("验证码凭证为空");
        }
        if (StrUtil.isEmpty(loginDto.getEmail())) {
            log.error("邮箱为空");
            return R.error("邮箱为空");
        }
        if (StrUtil.isEmpty(loginDto.getPassword())) {
            log.error("密码为空");
            return R.error("密码为空");
        }

        long codeKey  = loginDto.getCodeId();

        // 判断验证码是否正确
        String picCode = codeCache.get(CaffeineConstants.PIC + codeKey, key -> {
            log.error("验证码为空");
            return null;
        });

        if (StrUtil.isBlank(picCode) || !Objects.equals(picCode, loginDto.getPicCode())) {
            log.error("验证码错误");
            return R.error("验证码错误");
        }

        // 使用shiro进行安全校验
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken();

        usernamePasswordToken.setUsername(loginDto.getEmail());
        usernamePasswordToken.setPassword(loginDto.getPassword().toCharArray());
        try {
            subject.login(usernamePasswordToken);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("登录失败");
            return R.error("登录失败");
        }

        // 登录成功  删除验证码缓存
        codeCache.invalidate(CaffeineConstants.PIC + loginDto.getEmail());
        // 正确则返回token
        String token = MyJwtUtil.createToken(UserContext.getCurrentId());

        LoginVO loginVO = new LoginVO();
        loginVO.setUserId(UserContext.getCurrentId());
        loginVO.setToken(token);
        return R.success(loginVO);
    }

    /**
     * 用户退出 清理shiro用户权限信息等
     *
     * @return
     */
    @Override
    public R<String> logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return R.success("用户退出成功");
    }

    /**
     * 根据id查询用户
     * @param id
     * @return
     */
    @Override
    public UserVO getUserById(String id) {
        // 参数校验
        if (StrUtil.isEmpty(id)) {
            log.error("用户id为空");
            return null;
        }
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getId, id);
        wrapper.eq(User::getStatus, 1);

        User user = getOne(wrapper);
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);

        return userVO;
    }

    /**
     * 用户修改密码接口
     *
     * @param registerDto
     * @return
     */
    @Override
    public R<String> register(RegisterDto registerDto) {
        // 参数校验 验证码是否正确
        String errorMsg = checkRegisterDto(registerDto, 0);
        if (StrUtil.isNotEmpty(errorMsg)) {
            return R.error(errorMsg);
        }
        // 验证通过 生成盐信息 并加密密码
        String salt = getSalt();
        String password = getMd5PassWithSalt(registerDto, salt);

        User user = new User();
        user.setEmail(registerDto.getEmail());
        user.setUsername(registerDto.getUsername());
        user.setPassword(password);
        user.setSalt(salt);
        save(user);

        //  清理邮箱验证码的缓存
        codeCache.invalidate(CaffeineConstants.REGISTER_MAIL+registerDto.getEmail());

        // 登录成功, 生成token返回
        return R.success("注册成功");
    }



    private String getSalt() {
        return IdUtil.simpleUUID().substring(2, 10);
    }

    /**
     * registerDto参数校验
     * @param registerDto
     * @param type     0:注册参数校验    1:修改密码参数校验
     * @return
     */
    private String checkRegisterDto(RegisterDto registerDto, Integer type) {
        String errorMsg = "";
        // 参数校验 验证码是否正确
        if (ObjectUtil.isEmpty(registerDto)) {
            log.error("register 为null");
            errorMsg = "参数错误";
            return errorMsg;
        }
        // 对比邮件验证码
        if (ObjectUtil.isEmpty(registerDto.getMailCode())) {
            errorMsg = "验证码为空";
            return errorMsg;
        } else {
            // 从缓存中根据邮箱取出验证码
            String mailCode = codeCache.get(CaffeineConstants.UPDATE_MAIL + registerDto.getEmail(), key -> {
                log.error("mail验证码不存在");
                return null;
            });
            if (ObjectUtil.isNull(mailCode)) {
                errorMsg = "mail验证码不存在";
                return errorMsg;
            } else if (!Objects.equals(registerDto.getMailCode(), mailCode)) {
                log.error("mail验证码错误:缓存验证码={}, 输入验证码={}", mailCode, registerDto.getMailCode());
                errorMsg = "mail验证码错误";
                return errorMsg;
            }
        }

        if (StrUtil.isEmpty(registerDto.getUsername()) && type == 0) {
            log.error("用户名为空");
            errorMsg = "用户名为空";
            return errorMsg;
        }
        if (StrUtil.isEmpty(registerDto.getEmail())) {
            log.error("邮箱为空");
            errorMsg = "邮箱为空";
            return errorMsg;
        }
        if (StrUtil.isEmpty(registerDto.getPassword())) {
            log.error("密码为空");
            errorMsg = "密码为空";
            return errorMsg;
        }
        return errorMsg;
    }

}




