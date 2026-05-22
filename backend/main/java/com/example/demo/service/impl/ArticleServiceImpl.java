package com.example.demo.service.impl;

import com.example.demo.mapper.ArticleMapper;
import com.example.demo.pojo.Article;
import com.example.demo.pojo.PageBean;
import com.example.demo.service.ArticleService;
import com.example.demo.utils.ThreadLocalUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ChatLanguageModel chatLanguageModel;

    @Override
    @CacheEvict(value = "articleList", allEntries = true)
    public void add(Article article) {
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer)map.get("id");
        article.setCreateUser(userId);
        articleMapper.add(article);
        // 不在创建时自动调用generateSummary，避免新文章ID获取问题
        // 用户可以通过AI接口手动触发摘要生成
    }

    @Override
    @Cacheable(value = "articleList", key = "#root.target.getCurrentUserId() + ':' + #categoryId + ':' + #state + ':' + #pageNum + ':' + #pageSize")
    public PageBean<Article> list(Integer pageNum, Integer pageSize, Integer categoryId, String state) {
        //1、创建pageBean对象
        PageBean<Article> pb=new PageBean<>();

        //2、开启分页查询.
        PageHelper.startPage(pageNum,pageSize);//会自动拼接到sql后

        //mapper
        Map<String,Object> map=ThreadLocalUtil.get();
        Integer userId = (Integer)map.get("id");
        List<Article> as=articleMapper.list(userId,categoryId,state);

        //page提供了方法，可以将pagehelper分页查询后 得到总记录条数与当前页数据
        Page<Article> p=(Page<Article>) as;

        //把数据写到pageBean对象里
        pb.setTotal(p.getTotal());
        pb.setItems(p.getResult());

        return pb;
    }

    /**
     * 获取当前登录用户ID（用于缓存key）
     */
    public Integer getCurrentUserId() {
        Map<String,Object> map = ThreadLocalUtil.get();
        return (Integer) map.get("id");
    }

    @Override
    @Cacheable(value = "article",key = "#id")
    public Article selectById(Integer id) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer createUser = (Integer)map.get("id");
        Article as=articleMapper.selectById(id,createUser);
        return as;
    }

    @Override
    @CacheEvict(value = {"article", "articleList"}, allEntries = true)
    public void update(Article article) {
        article.setUpdateTime(LocalDateTime.now());
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer createUser = (Integer)map.get("id");
        article.setCreateUser(createUser);
        articleMapper.update(article);
    }

    @Override
    @CacheEvict(value = {"article", "articleList"}, allEntries = true)
    public void delete(Integer id) {
        Map<String,Object> map=ThreadLocalUtil.get();
        Integer createUser = (Integer)map.get("id");
        articleMapper.delete(id,createUser);
    }

    @Override
    @CacheEvict(value = {"article", "articleList"}, allEntries = true)
    public String generateSummary(Integer articleId, boolean forceRefresh) {
        // 1. 获取当前登录用户
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");

        // 2. 权限验证：查询文章（带createUser检查）
        Article article = articleMapper.selectById(articleId, userId);
        if (article == null) {
            throw new RuntimeException("无权访问该文章或文章不存在");
        }

        // 3. 如果不需要强制刷新且已有摘要，直接返回
        if (!forceRefresh && article.getSummary() != null && !article.getSummary().isEmpty()) {
            return article.getSummary();
        }

        // 4. 获取文章内容（使用无权限限制的查询）
        Article fullArticle = articleMapper.selectByIdForAI(articleId);
        String content = fullArticle.getContent();

        // start-dev.bat. 内容截断（避免超过API token限制）
        if (content.length() > 4000) {
            content = content.substring(0, 4000) + "...";
        }

        // 6. 构建Prompt并调用AI生成摘要
        String prompt = "请为以下文章生成一个简洁的中文摘要，控制在100字以内：\n\n" + content;
        String summary = chatLanguageModel.generate(prompt);

        // 7. 更新摘要到数据库
        articleMapper.updateSummary(articleId, summary, userId);

        return summary;
    }
}