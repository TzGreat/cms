package com.example.demo.service;

import com.example.demo.pojo.Category;

import java.util.List;

public interface CategoryService {
    void add(Category category);

    List<Category> list();

    //根据id查询分类情况
    Category findById(Integer id);

    //更新
    void update(Category category);

    //删除分类
    void delete(Integer id);
}
