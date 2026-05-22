package com.example.demo.controller;

import com.example.demo.pojo.Result;
import com.example.demo.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class AIController {
    @Autowired
    private ArticleService articleService;

    //刷新文章摘要
    @PostMapping("/summary")
    public Result<String> refreshSummary(@RequestParam Integer articleId) {
        try{
            String summary = articleService.generateSummary(articleId, true);
            return Result.success(summary);
        }catch (Exception e){
            return Result.error("刷新摘要失败");
        }
    }

    //获取文章摘要
    @GetMapping("/summary")
    public Result<String> getSummary(@RequestParam Integer articleId) {
        try {
            String summary = articleService.generateSummary(articleId, false);
            return Result.success(summary);
        } catch (Exception e) {
            return Result.error("获取摘要失败: " + e.getMessage());
        }
    }

    //测试
    @GetMapping("/health")
    public Result<String> healthCheck() {
        return Result.success("AI服务运行正常");
    }
}
