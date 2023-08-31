package com.ycg.daily.service;

import com.ycg.daily.common.R;
import com.ycg.daily.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ycg.daily.pojo.dto.LoginDto;
import com.ycg.daily.pojo.dto.RegisterDto;
import com.ycg.daily.pojo.vo.LoginVO;
import com.ycg.daily.pojo.vo.UserVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author lenovo
* @description 针对表【daily_user(用户账号的基本信息)】的数据库操作Service
* @createDate 2023-08-21 11:47:19
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册功能
     * 发送邮箱  返回 token
     * @param registerDto
     * @return
     */
    R<String> register(RegisterDto registerDto);

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    R<UserVO> updateUser(User user);

    /**
     * 用户登录, 判断token
     * @param loginDto
     * @return
     */
    R<LoginVO> login(LoginDto loginDto, HttpServletRequest request);

    /**
     * 用户退出
     * @return
     */
    R<String> logout();

    /**
     * 根据id查询用户
     * @param id
     * @return
     */
    UserVO getUserById(String id);
}
