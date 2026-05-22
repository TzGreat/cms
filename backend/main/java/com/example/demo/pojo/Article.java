package com.example.demo.pojo;

import com.example.demo.anno.State;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Article {
    // 主键ID
    private Integer id;
    // 文章标题
    @NotEmpty
    @Pattern(regexp = "^[\\s\\S]{1,100}$")
    private String title;
    // 文章内容
    @NotEmpty
    private String content;
    // 文章封面（对应数据库cover_img）
    @NotEmpty
    @URL
    private String coverImg;
    // 文章状态（草稿/已发布）
    @State
    private String state;
    // 分类ID（对应数据库category_id）
    @NonNull
    private Integer categoryId;
    // 创建人ID（对应数据库create_user）
    private Integer createUser;
    // 创建时间
    private LocalDateTime createTime;
    // 更新时间
    private LocalDateTime updateTime;
    //摘要
    private String summary;
}