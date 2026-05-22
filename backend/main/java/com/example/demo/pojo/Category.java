package com.example.demo.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.groups.Default;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
public class Category {
    // 主键ID
    private Integer id;
    // 分类名称（对应数据库category_name）
    @NotEmpty
    private String categoryName;
    // 分类别名（对应数据库category_alias）
    @NotEmpty
    private String categoryAlias;
    // 创建人ID（对应数据库create_user）
    private Integer createUser;
    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    public interface add extends Default{

    }
    public interface update extends Default{

    }
}