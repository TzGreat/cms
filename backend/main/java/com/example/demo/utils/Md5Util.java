package com.example.demo.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具类（黑马大事件项目标准实现）
 */
public class Md5Util {

    /**
     * 16进制字符映射表：用于将字节转成16进制字符串
     * （和Apache校验文件用的默认组合一致）
     */
    protected static char hexDigits[] = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    /**
     * MessageDigest实例：Java提供的消息摘要工具，用于实现MD5算法
     */
    protected static MessageDigest messagedigest = null;

    /**
     * 静态代码块：初始化MD5算法实例（全局只初始化1次）
     */
    static {
        try {
            messagedigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsaex) {
            System.err.println(Md5Util.class.getName() + " 初始化失败：MessageDigest不支持MD5算法");
            nsaex.printStackTrace();
        }
    }

    /**
     * 对外工具方法：对字符串进行MD5加密
     * @param s 明文字符串（如用户输入的密码）
     * @return 加密后的32位MD5字符串
     */
    public static String getMD5String(String s) {
        // 调用重载方法，把字符串转成字节数组后加密
        return getMD5String(s.getBytes());
    }

    /**
     * 核心实现：对字节数组进行MD5加密
     * @param bytes 要加密的字节数组
     * @return 加密后的32位MD5字符串
     */
    public static String getMD5String(byte[] bytes) {
        // 更新摘要（传入要加密的字节数组）
        messagedigest.update(bytes);
        // 计算最终的MD5摘要（返回16字节的byte数组）
        byte[] digest = messagedigest.digest();

        // 把16字节的摘要转成32位的16进制字符串
        StringBuilder sb = new StringBuilder(digest.length * 2);
        for (byte b : digest) {
            // 1. 取字节的高4位，转成16进制字符
            sb.append(hexDigits[(b >> 4) & 0x0F]);
            // 2. 取字节的低4位，转成16进制字符
            sb.append(hexDigits[b & 0x0F]);
        }
        return sb.toString();
    }

    /**
     * 密码校验工具方法：判断明文密码和加密后的MD5密码是否匹配
     * @param password 明文密码（用户登录时输入的密码）
     * @param md5PwdStr 数据库中存储的加密后的MD5密码
     * @return 匹配返回true，否则返回false
     */
    public static boolean checkPassword(String password, String md5PwdStr) {
        // 把用户输入的明文密码加密，再和数据库里的MD5密码对比
        String encryptedPwd = getMD5String(password);
        return encryptedPwd.equals(md5PwdStr);
    }

    // 测试方法：直接运行main方法就能看效果
    public static void main(String[] args) {
        String plainPwd = "123456";
        String md5Pwd = getMD5String(plainPwd);

        System.out.println("明文密码：" + plainPwd);
        System.out.println("MD5加密后：" + md5Pwd);
        System.out.println("校验结果：" + checkPassword("123456", md5Pwd)); // 输出true
        System.out.println("校验结果：" + checkPassword("1234567", md5Pwd)); // 输出false
    }
}