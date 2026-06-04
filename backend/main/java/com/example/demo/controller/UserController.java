package com.example.demo.controller;

import com.example.demo.pojo.Result;
import com.example.demo.pojo.User;
import com.example.demo.service.UserService;
import com.example.demo.utils.JwtUtil;
import com.example.demo.utils.Md5Util;
import com.example.demo.utils.ThreadLocalUtil;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/register")
    public Result register(@Pattern(regexp = "^\\w{5,16}$")
                               String username,@Pattern(regexp = "^\\w{5,16}$") String password) {
        User u=userService.findByUserName(username);
        if(u==null){
            //没有占用
            userService.register(username,password);
            return Result.success();
        }else {
            return Result.error("用户名被占用");
        }
    }

    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> loginData){
        String username = loginData.get("username");
        String password = loginData.get("password");
        //查询用户名
        User loginUser=userService.findByUserName(username);
        //判断用户是否存在
        if(loginUser==null){
            return Result.error("用户名错误");
        }
        //验证密码
        if(Md5Util.getMD5String(password).equals(loginUser.getPassword())){
            //登录成功
            //生成token
            Map<String,Object> claims=new HashMap<>();
            claims.put("id",loginUser.getId());
            claims.put("username",loginUser.getUsername());
            String token= JwtUtil.genToken(claims);
            return Result.success(token);
        }
        return Result.error("密码错误");
    }

    @GetMapping("/userInfo")
    public Result<User> userInfo(){
        //用户名查询用户
        Map<String,Object> map= ThreadLocalUtil.get();
        String username=(String) map.get("username");

        User user=userService.findByUserName(username);
        return Result.success(user);
    }

    @PutMapping("/update")
    public Result update(@RequestBody @Validated User user){
        userService.update(user);
        return Result.success();
    }

    @PatchMapping("updateAvatar")
    public Result updateAvatar(@RequestParam @URL String avatarUrl){
        userService.updateAvatar(avatarUrl);
        return Result.success();
    }

    @PatchMapping("/updatePwd")
    public Result updatePwd(@RequestBody Map<String,String> params){
        String oldPwd=params.get("old_pwd");
        String newPwd=params.get("new_pwd");
        String rePwd=params.get("re_pwd");

        if(!StringUtils.hasLength(oldPwd) || !StringUtils.hasLength(newPwd) || !StringUtils.hasLength(rePwd)){
            return Result.error("参数错误");
        }
        //获取用户名
        Map<String,Object> map= ThreadLocalUtil.get();
        String username=(String) map.get("username");
        //获取用户信息
        User loginUser=userService.findByUserName(username);
        //判断密码是否正确
        if(!loginUser.getPassword().equals(Md5Util.getMD5String(oldPwd))){
            return Result.error("原密码错误");
        }
        //判断两次输入是否一致
        if(!rePwd.equals(newPwd)){
            return Result.error("两次密码填写不一致");
        }
        userService.updatePwd(newPwd);
        return Result.success();
    }
}