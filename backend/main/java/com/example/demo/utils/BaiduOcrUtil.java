package com.example.demo.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 百度OCR工具类
 * 使用HTTP API直接调用，避免SDK依赖问题
 */
@Component
public class BaiduOcrUtil {

    @Value("${baidu.ocr.api-key}")
    private String apiKey;

    @Value("${baidu.ocr.secret-key}")
    private String secretKey;

    // 获取AccessToken的URL
    private static final String TOKEN_URL = "https://aip.baidubce.com/oauth/2.0/token";
    // 通用文字识别URL
    private static final String OCR_URL = "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic";

    // 缓存的AccessToken
    private String accessToken;
    private long tokenExpireTime;

    /**
     * 图片文字识别
     * @param imageBytes 图片字节数组
     * @return 识别的文本内容
     */
    public String recognizeText(byte[] imageBytes) {
        try {
            // 获取AccessToken
            String token = getAccessToken();
            if (!StringUtils.hasText(token)) {
                return "";
            }

            // 构建请求参数
            String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);
            String params = "image=" + java.net.URLEncoder.encode(imageBase64, "UTF-8")
                    + "&language_type=CHN_ENG";

            // 构建请求
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OCR_URL + "?access_token=" + token))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(params, StandardCharsets.UTF_8))
                    .build();

            // 发送请求
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 解析响应
            return parseResponse(response.body());

        } catch (Exception e) {
            System.err.println("OCR识别异常: " + e.getMessage());
            return "";
        }
    }

    /**
     * 获取AccessToken
     */
    private synchronized String getAccessToken() throws IOException, InterruptedException {
        // 检查token是否过期
        if (StringUtils.hasText(accessToken) && System.currentTimeMillis() < tokenExpireTime) {
            return accessToken;
        }

        // 构建请求参数
        String params = "grant_type=client_credentials"
                + "&client_id=" + apiKey
                + "&client_secret=" + secretKey;

        // 发送请求
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TOKEN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(params, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // 解析响应
        String body = response.body();
        if (body.contains("access_token")) {
            // 提取access_token
            accessToken = extractValue(body, "access_token");
            // 提取expires_in，计算过期时间
            String expiresInStr = extractValue(body, "expires_in");
            long expiresIn = StringUtils.hasText(expiresInStr) ? Long.parseLong(expiresInStr) : 3000;
            tokenExpireTime = System.currentTimeMillis() + (expiresIn - 60) * 1000; // 提前1分钟过期
            return accessToken;
        }

        return null;
    }

    /**
     * 从JSON字符串中提取值（简单实现）
     */
    private String extractValue(String json, String key) {
        int start = json.indexOf("\"" + key + "\":\"");
        if (start == -1) {
            // 尝试不带引号的数字类型
            start = json.indexOf("\"" + key + "\":");
            if (start == -1) return null;
            start += key.length() + 3;
            int end = json.indexOf(",", start);
            if (end == -1) end = json.indexOf("}", start);
            return json.substring(start, end).trim();
        }
        start += key.length() + 4;
        int end = json.indexOf("\"", start);
        if (end == -1) return null;
        return json.substring(start, end);
    }

    /**
     * 解析OCR响应
     */
    private String parseResponse(String response) {
        StringBuilder result = new StringBuilder();

        // 简单解析JSON中的words_result
        int start = response.indexOf("\"words_result\"");
        if (start == -1) return "";

        // 找到数组开始
        start = response.indexOf("[", start);
        if (start == -1) return "";

        // 找到数组结束
        int end = findMatchingBracket(response, start);
        if (end == -1) return "";

        String arrayStr = response.substring(start + 1, end);

        // 提取每个words字段
        int pos = 0;
        while ((pos = arrayStr.indexOf("\"words\":\"", pos)) != -1) {
            pos += 9; // 跳过 "\"words\":\""
            int wordEnd = arrayStr.indexOf("\"", pos);
            if (wordEnd != -1) {
                result.append(arrayStr.substring(pos, wordEnd)).append("\n");
                pos = wordEnd;
            }
        }

        return result.toString().trim();
    }

    /**
     * 查找匹配的括号
     */
    private int findMatchingBracket(String str, int start) {
        int depth = 1;
        for (int i = start + 1; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '[') depth++;
            else if (c == ']') depth--;
            if (depth == 0) return i;
        }
        return -1;
    }
}