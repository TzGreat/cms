package com.example.demo.controller;

import com.example.demo.pojo.Article;
import com.example.demo.pojo.CrawlRequest;
import com.example.demo.pojo.CrawlResult;
import com.example.demo.pojo.Result;
import com.example.demo.service.ArticleService;
import com.example.demo.service.CrawlerService;
import com.example.demo.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/crawler")
public class CrawlerController {
    @Autowired
    private CrawlerService crawlerService;

    @Autowired
    private ArticleService articleService;

    //抓取文章
    @PostMapping("/fetch")
    public Result<CrawlResult> fetchArticle(@RequestBody CrawlRequest request) {
        String url = request.getUrl();

        // 检查是否已抓取
        if (crawlerService.isDuplicate(url)) {
            return Result.error("该文章已抓取过");
        }

        CrawlResult result = crawlerService.fetchArticle(url);
        return Result.success(result);
    }

    //抓取并保存文章
    @PostMapping("/save")
    public Result<String> fetchAndSave(@RequestBody CrawlRequest request) {
        String url = request.getUrl();

        // 检查是否已抓取
        if (crawlerService.isDuplicate(url)) {
            return Result.error("该文章已抓取过");
        }

        // 抓取文章
        CrawlResult crawlResult = crawlerService.fetchArticle(url);

        // 创建文章对象
        Article article = new Article();
        article.setTitle(crawlResult.getTitle());
        article.setContent(crawlResult.getContent());
        // 如果没有抓取到封面图片，设置默认图片
        String coverImg = crawlResult.getCoverImage();
        if (coverImg == null || coverImg.isEmpty()) {
            coverImg = "https://via.placeholder.com/1200x630?text=No+Cover";
        }
        article.setCoverImg(coverImg);
        article.setState("d"); // 草稿状态

        // 设置分类（使用传入的分类或默认分类）
        if (request.getCategoryId() != null) {
            article.setCategoryId(request.getCategoryId());
        } else {
            // 获取用户的第一个分类作为默认分类
            article.setCategoryId(getDefaultCategoryId());
        }

        // 设置创建用户
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        article.setCreateUser(userId);

        // 保存文章
        articleService.add(article);

        // 记录抓取记录
        crawlerService.saveCrawlRecord(url);

        return Result.success("文章抓取并保存成功");
    }

    /**
     * 获取用户的默认分类ID（第一个分类）
     */
    private Integer getDefaultCategoryId() {
        // 这里可以根据实际需求修改，比如从用户的分类列表中获取第一个
        // 暂时返回1作为默认分类ID
        return 1;
    }
}