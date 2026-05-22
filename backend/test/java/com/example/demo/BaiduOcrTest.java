package java.com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 百度OCR API测试类
 */
@Component
public class BaiduOcrTest {

    @Value("${baidu.ocr.api-key}")
    private String apiKey;

    @Value("${baidu.ocr.secret-key}")
    private String secretKey;

    private static final String TOKEN_URL = "https://aip.baidubce.com/oauth/2.0/token";
    private static final String OCR_URL = "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic";

    /**
     * 测试获取AccessToken
     */
    public void testAccessToken() {
        try {
            String params = "grant_type=client_credentials"
                    + "&client_id=" + apiKey
                    + "&client_secret=" + secretKey;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(TOKEN_URL))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(params, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("=== AccessToken测试结果 ===");
            System.out.println("状态码: " + response.statusCode());
            System.out.println("响应内容: " + response.body());

            if (response.body().contains("access_token")) {
                System.out.println("✅ AccessToken获取成功！");
            } else {
                System.out.println("❌ AccessToken获取失败！");
            }

        } catch (Exception e) {
            System.err.println("测试异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试OCR识别（使用测试图片）
     */
    public void testOcrRecognition() {
        try {
            // 1. 先获取AccessToken
            String token = getAccessToken();
            if (token == null) {
                System.out.println("❌ 无法获取AccessToken，OCR测试终止");
                return;
            }

            // 2. 创建一个简单的测试图片（1x1像素的白色图片）
            byte[] testImage = createTestImage();
            String imageBase64 = Base64.getEncoder().encodeToString(testImage);

            // 3. 调用OCR API
            String params = "image=" + java.net.URLEncoder.encode(imageBase64, "UTF-8")
                    + "&language_type=CHN_ENG";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OCR_URL + "?access_token=" + token))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(params, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("=== OCR识别测试结果 ===");
            System.out.println("状态码: " + response.statusCode());
            System.out.println("响应内容: " + response.body());

            if (response.body().contains("words_result")) {
                System.out.println("✅ OCR API调用成功！");
            } else {
                System.out.println("❌ OCR API调用失败！");
            }

        } catch (Exception e) {
            System.err.println("OCR测试异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 获取AccessToken
     */
    private String getAccessToken() throws Exception {
        String params = "grant_type=client_credentials"
                + "&client_id=" + apiKey
                + "&client_secret=" + secretKey;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TOKEN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(params, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();

        if (body.contains("access_token")) {
            // 简单提取access_token
            int start = body.indexOf("\"access_token\":\"") + 16;
            int end = body.indexOf("\"", start);
            return body.substring(start, end);
        }
        return null;
    }

    /**
     * 创建一个简单的测试图片（1x1像素的白色PNG）
     */
    private byte[] createTestImage() {
        // 这是一个1x1像素的白色PNG图片的Base64编码
        String base64Image = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==";
        return Base64.getDecoder().decode(base64Image);
    }
}