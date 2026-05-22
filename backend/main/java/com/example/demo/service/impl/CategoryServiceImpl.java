package com.example.demo.service.impl;

import com.example.demo.mapper.CategoryMapper;
import com.example.demo.pojo.Category;
import com.example.demo.service.CategoryService;
import com.example.demo.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    @CacheEvict(value = "categoryList", key = "#root.target.getCurrentUserId()")
    public void add(Category category) {
        //补充属性
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId=(Integer) map.get("id");
        category.setCreateUser(userId);
        categoryMapper.add(category);
    }

    /**
     * 获取当前登录用户ID（用于缓存key）
     */
    public Integer getCurrentUserId() {
        Map<String,Object> map = ThreadLocalUtil.get();
        return (Integer) map.get("id");
    }

    @Override
    @Cacheable(value = "categoryList", key = "#root.target.getCurrentUserId()")
    public List<Category> list() {
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId=(Integer) map.get("id");
        return categoryMapper.list(userId);
    }

    @Override
    @Cacheable(value = "category",key = "#id")
    public Category findById(Integer id) {
        Category c=categoryMapper.findById(id);
        return c;
    }

    @Override
    @CacheEvict(value = "categoryList", key = "#root.target.getCurrentUserId()")
    public void update(Category category) {
        category.setUpdateTime(LocalDateTime.now());
        categoryMapper.update(category);
    }

    @Override
    @CacheEvict(value = "categoryList", key = "#root.target.getCurrentUserId()")
    public void delete(Integer id) {
        categoryMapper.delete(id);
    }


}