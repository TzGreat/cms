package com.example.demo.service;

import com.example.demo.pojo.Article;
import com.example.demo.pojo.PageBean;

public interface ArticleService {
    //新增文章
    void add(Article article);

    //条件分页列表查询
    PageBean<Article> list(Integer pageNum, Integer pageSize, Integer categoryId, String state);

    //id查询文章
    Article selectById(Integer id);

    //更新文章
    void update(Article article);

    //删除文章
    void delete(Integer id);

    //更新摘要
    String generateSummary(Integer articleId, boolean forceRefresh);
}
