package com.example.demo.service.impl;

import com.example.demo.pojo.CrawlResult;
import com.example.demo.service.CrawlerService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CrawlerServiceImpl implements CrawlerService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Redis key前缀
    private static final String CRAWL_URL_KEY = "crawl:urls:";
    private static final long EXPIRATION_DAYS = 30;

    @Override
    public CrawlResult fetchArticle(String url) {
        CrawlResult result=new CrawlResult();
        result.setSourceUrl(url);
        result.setSourceType(detectSourceType(url));

        try{
            //设置请求头，模拟浏览器
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/start-dev.bat.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                    .referrer(url)
                    .timeout(30000)
                    .get();

            // 提取标题
            String title=extractTitle(document);
            result.setTitle(title);

            //获取正文
            String content=extractContent(document);
            result.setContent(content);

            //提取照片
            String coverImg=extractCoverImg(document);
            result.setCoverImage(coverImg);

        }catch (Exception e){
            throw new RuntimeException("抓取失败"+e.getMessage());
        }
        return result;
    }

    @Override
    public boolean isDuplicate(String url) {
        String key = CRAWL_URL_KEY + getUrlHash(url);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public void saveCrawlRecord(String url) {
        String key = CRAWL_URL_KEY + getUrlHash(url);
        redisTemplate.opsForValue().set(key, url, EXPIRATION_DAYS, TimeUnit.HOURS);
    }

    /**
     * 检测来源类型
     */
    private String detectSourceType(String url) {
        if (url.contains("csdn.net")) return "CSDN";
        if (url.contains("juejin.cn")) return "掘金";
        if (url.contains("zhihu.com") || url.contains("zhuanlan.zhihu.com")) return "知乎";
        if (url.contains("segmentfault.com")) return "思否";
        if (url.contains("oschina.net")) return "开源中国";
        if (url.contains("blog.csdn.net")) return "CSDN博客";
        return "其他";
    }

    /**
     * 提取标题
     */
    private String extractTitle(Document document) {
        // 优先尝试常见的标题选择器
        String[] selectors = {
                "h1.article-title",
                "h1.title-article",
                "h1.Post-Title",
                "h1.article__title",
                ".article-title",
                ".title-article",
                "article h1",
                ".post-title",
                "h1"
        };

        for (String selector : selectors) {
            Element element = document.selectFirst(selector);
            if (element != null && !element.text().trim().isEmpty()) {
                return element.text().trim();
            }
        }

        // 如果都没找到，返回页面title
        String title = document.title();
        // 移除常见的网站后缀
        title = title.replaceAll("[-_|].*CSDN.*", "")
                .replaceAll("[-_|].*掘金.*", "")
                .replaceAll("[-_|].*知乎.*", "")
                .trim();
        return title;
    }

    /**
     * 提取正文内容
     */
    private String extractContent(Document document) {
        // 常见的正文选择器
        String[] selectors = {
                "article",
                ".article-content",
                "#article_content",
                ".article-body",
                ".post-content",
                ".markdown-body",
                ".content",
                "#content",
                ".main-content",
                ".blog-content"
        };

        for (String selector : selectors) {
            Element element = document.selectFirst(selector);
            if (element != null) {
                // 清理内容：移除脚本、样式、广告等
                cleanElement(element);
                String text = element.text();
                if (text.length() > 50) {
                    return text;
                }
            }
        }

        // 如果都没找到，尝试获取body中的主要文本
        Element body = document.body();
        if (body != null) {
            cleanElement(body);
            return body.text();
        }

        return "";
    }

    /**
     * 清理元素，移除不需要的内容
     */
    private void cleanElement(Element element) {
        // 移除脚本、样式、iframe
        element.select("script, style, iframe, noscript").remove();
        // 移除广告相关元素
        element.select("[class*=ad], [class*=AD], [id*=ad], [id*=AD]").remove();
        element.select(".advertisement, .banner, .footer, .header").remove();
        // 移除分享、评论、推荐等模块
        element.select(".share, .comment, .recommend, .related, .aside").remove();
    }

    /**
     * 提取封面图片
     */
    private String extractCoverImg(Document document) {
        String[] selectors = {
                "meta[property=og:image]",
                "meta[name=og:image]",
                "meta[property=twitter:image]",
                ".article-cover img",
                ".cover-image img",
                "article img:first-of-type",
                ".post-cover img"
        };

        for (String selector : selectors) {
            Elements elements = document.select(selector);
            if (!elements.isEmpty()) {
                Element element = elements.first();
                String imgUrl = element.tagName().equals("meta")
                        ? element.attr("content")
                        : element.attr("src");
                if (imgUrl != null && !imgUrl.isEmpty() && (imgUrl.startsWith("http") || imgUrl.startsWith("//"))) {
                    if (imgUrl.startsWith("//")) {
                        imgUrl = "https:" + imgUrl;
                    }
                    return imgUrl;
                }
            }
        }
        return null;
    }

    /**
     * 计算URL的哈希值用于存储
     */
    private String getUrlHash(String url) {
        return String.valueOf(url.hashCode());
    }
}

