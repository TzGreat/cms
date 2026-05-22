package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrawlResult {
    private String title;
    private String content;
    private String coverImage;
    private String sourceUrl;
    private String sourceType;
}
