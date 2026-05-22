package com.example.demo.utils;

/**
 * ThreadLocal工具类：用于存储当前登录用户信息
 * 作用：在同一请求线程内，跨组件传递用户信息，避免频繁传参或重复查库
 */
public class ThreadLocalUtil {

    /**
     * 线程本地变量：每个请求线程有独立的副本，不同用户请求互不干扰
     * 这里用来存储当前登录用户的关键信息（通常是用户ID）
     */
    private static final ThreadLocal<Object> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 从ThreadLocal中获取存储的值
     * @param <T> 泛型适配：支持获取Integer用户ID、User对象等不同类型
     * @return 存储的用户信息
     */
    @SuppressWarnings("unchecked") // 抑制类型转换警告
    public static <T> T get() {
        return (T) THREAD_LOCAL.get();
    }

    /**
     * 向ThreadLocal中存储值
     * @param value 要存储的用户信息（通常是当前登录用户的ID）
     */
    public static void set(Object value) {
        THREAD_LOCAL.set(value);
    }

    /**
     * 清除ThreadLocal中的值，**必须在请求结束后调用**
     * 作用：1. 防止线程复用导致的数据污染  2. 避免内存泄漏
     */
    public static void remove() {
        THREAD_LOCAL.remove();
    }

}