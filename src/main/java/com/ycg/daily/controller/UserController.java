package com.ycg.daily.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.http.server.HttpServerResponse;
import com.ycg.daily.common.R;
import com.ycg.daily.pojo.User;
import com.ycg.daily.pojo.dto.LoginDto;
import com.ycg.daily.pojo.dto.RegisterDto;
import com.ycg.daily.pojo.vo.LoginVO;
import com.ycg.daily.pojo.vo.UserVO;
import com.ycg.daily.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册接口
     * @param registerDto 邮箱, 用户名, 密码 ,验证码
     * @return
     */
    @PostMapping("/register")
    public R<String> register(@RequestBody RegisterDto registerDto) {
        return userService.register(registerDto);
    }

    /**
     * 用户登录
     * @param loginDto 邮箱 密码 验证码
     * @return
     */
    @PostMapping("/login")
    public R<LoginVO> login(@RequestBody LoginDto loginDto, HttpServletRequest request) {
        return userService.login(loginDto,request);
    }

    /**
     * 用户退出, 清空shiro的用户缓存
     */
    @PostMapping("/logout")
    public R<String> logout() {
        return userService.logout();
    }

    /**
     * 根据id查询用户详细信息 这里应该使用vo, 因为不是所有的数据都要的, 减少网络传输
     * 但是这是个小项目, 不管了
     */
    @GetMapping("/{id}")
    public R<UserVO> getUserById(@PathVariable("id") String id) {
        UserVO user = userService.getUserById(id);
        return R.success(user);
    }

    /**
     * 修改用户信息接口
     * @param user  前端传过来的user, 包括id和要修改的数据
     * @return
     */
    @PutMapping("/updateUser")
    public R<UserVO> updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }


    @RequiresRoles("admin")
    @DeleteMapping("/deleteUser/{id}")
    public R<String> deleteUser(@PathVariable("id") Integer id) {
        userService.removeById(id);
        return R.success("删除用户成功");
    }


}
