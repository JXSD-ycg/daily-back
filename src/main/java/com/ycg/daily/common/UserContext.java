package com.ycg.daily.common;

import com.ycg.daily.pojo.vo.UserVO;

/**
 * 请求上下文, 再一次请求可以获取当前用户的id
 */
public class UserContext {

    private static final ThreadLocal<Integer> threadLocal = new ThreadLocal<>();

    /**
     * 获取当前请求id
     * @return
     */
    public static Integer getCurrentId() {
        return threadLocal.get();
    }

    /**
     * 设置当前请求id
     * @param id
     */
    public static void setCurrentId(Integer id) {
        threadLocal.set(id);
    }

}
