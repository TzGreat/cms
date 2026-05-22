package com.example.demo.pojo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NonNull;
import net.minidev.json.annotate.JsonIgnore;

import java.time.LocalDateTime;

@Data // Lombok注解，自动生成getter/setter/toString等
public class User {
    // 主键ID
    private Integer id;
    // 用户名
    private String username;
    // 密码
    @JsonIgnore
    private String password;
    // 昵称
    @NotEmpty
    @Pattern(regexp = "\\w{1,18}$")
    private String nickname;
    // 邮箱
    @Email
    @NotEmpty
    private String email;
    // 用户头像（对应数据库user_pic）
    private String userPic;
    // 创建时间（对应数据库create_time）
    private LocalDateTime createTime;
    // 更新时间（对应数据库update_time）
    private LocalDateTime updateTime;
}