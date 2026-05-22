package com.example.demo.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.util.Date;
import java.util.Map;

/**
 * JWT工具类：生成Token、解析Token
 */
public class JwtUtil {

    /**
     * 密钥：用于签名和验证Token（生产环境请替换为自己的安全密钥）
     * 注意：HMAC256算法要求密钥长度至少32位，太短会有安全风险
     */
    private static final String KEY = "tz";

    /**
     * 接收业务数据，生成Token并返回
     * @param claims 业务数据（如用户ID、用户名）
     * @return 生成的JWT Token
     */
    public static String genToken(Map<String, Object> claims) {
        // 1. 设置过期时间（当前时间 + 12小时）
        long expireTime = System.currentTimeMillis() + 1000 * 60 * 60 * 12;
        // 2. 生成Token
        return JWT.create()
                // 存放业务数据
                .withClaim("claims", claims)
                // 设置过期时间
                .withExpiresAt(new Date(expireTime))
                // 签名（使用HMAC256算法和密钥）
                .sign(Algorithm.HMAC256(KEY));
    }

    /**
     * 接收Token，验证Token并返回业务数据
     * @param token 前端传来的Token
     * @return 业务数据（如用户ID、用户名）
     * @throws RuntimeException Token无效（过期、篡改、格式错误等）
     */
    public static Map<String, Object> parseToken(String token) {
        return JWT.require(Algorithm.HMAC256(KEY))
                .build()
                .verify(token)
                .getClaim("claims")
                .asMap();
    }
}