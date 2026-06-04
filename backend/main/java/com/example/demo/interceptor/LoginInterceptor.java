package com.example.demo.interceptor;

import com.example.demo.utils.JwtUtil;
import com.example.demo.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token=request.getHeader("Authorization");
        //令牌验证
        try{
            // 如果token以"Bearer "开头，去掉前缀
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            Map<String,Object> claims = JwtUtil.parseToken(token);
            ThreadLocalUtil.set(claims);
            //放行
            return true;
        }catch (Exception e){
            response.setStatus(401);
            //不放行
            return false;
        }
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadLocalUtil.remove();
    }
}