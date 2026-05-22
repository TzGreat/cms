package com.example.demo.service;

import com.example.demo.pojo.CrawlResult;

public interface CrawlerService {
    //抓取文章
    CrawlResult fetchArticle(String url);

    //检查是否抓取果
    boolean isDuplicate(String url);

    //保存抓取记录
    void saveCrawlRecord(String url);

}
