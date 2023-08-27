package com.ycg.daily.util;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.ycg.daily.realm.TokenFilter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MyJwtUtil {

    /**
     * 签名
     */
    private static final String singer = "ycg";

    /**
     * token有效期为 7天
     */
    private static final long expireTime = 1000 * 60 * 60 * 24 * 7;

    /**
     * 根据 id 获取token  token有效时间7天
     * @param id
     * @return
     */
    public static String createToken(Integer id) {
        Map<String, Object> map = new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;
            {
                put("userId", id);
                // 过期时间 7天
                put("expire_time", System.currentTimeMillis() + expireTime);
            }
        };
        return JWTUtil.createToken(map, singer.getBytes());
    }

    /**
     * 验证token
     *  1. 签名是否有效
     *  2. 是否超过过期时间  统一抛出运行时异常
     * @param token
     * @return
     */
    public static boolean verify(String token) {
        if (!JWTUtil.verify(token, singer.getBytes())) {
            log.error("签名失效");
            throw new RuntimeException("签名失效");
        }
        long futureTime = Long.parseLong(JWTUtil.parseToken(token).getPayload("expire_time").toString());
        if (futureTime - System.currentTimeMillis() < 0) {
            log.error("token已过期");
            throw new RuntimeException("token以过期");
        }
        return true;
    }

//    public static void main(String[] args) {
//        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHBpcmVfdGltZSI6MTY5MzI5NDYxNzk1OCwidXNlcklkIjo3fQ.zgeW5zllit5QFOWKmN8PGfvNGM7Q2qFPuSDUxJghdAg";
//        System.out.println(Long.valueOf(JWTUtil.parseToken(token).getPayload("expire_time").toString()));
//    }

    /**
     * 解析token 获取用户id
     * @param token
     * @return
     */
    public static Integer getId(String token) {
        if (verify(token)) {
            return Integer.valueOf(JWT.of(token).getPayload("userId").toString());
        }
        return null;
    }

}
