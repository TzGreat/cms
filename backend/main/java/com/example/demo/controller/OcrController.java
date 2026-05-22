package com.example.demo.controller;

import com.example.demo.pojo.Result;
import com.example.demo.service.OcrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping("/ocr")
public class OcrController {

    @Autowired
    private OcrService ocrService;

    /**
     * 方式一：上传图片文件（推荐，适合网页使用）
     */
    @PostMapping("/upload")
    public Result<String> uploadAndRecognize(@RequestParam("file") MultipartFile file) {
        try {
            // 检查文件
            if (file.isEmpty()) {
                return Result.error("请选择要上传的图片");
            }

            // 检查文件类型
            String contentType = file.getContentType();
            if (!contentType.startsWith("image/")) {
                return Result.error("请上传有效的图片文件");
            }

            // 读取文件内容并识别
            byte[] imageBytes = file.getBytes();
            String text = ocrService.recognize(imageBytes);

            if (text.isEmpty()) {
                return Result.error("识别失败，未检测到文字");
            }
            return Result.success(text);

        } catch (IOException e) {
            return Result.error("文件读取失败: " + e.getMessage());
        } catch (Exception e) {
            return Result.error("识别失败: " + e.getMessage());
        }
    }

    /**
     * 方式二：Base64编码图片（保留原有接口）
     */
    @PostMapping("/recognize")
    public Result<String> recognize(@RequestBody String imageBase64) {
        try {
            String text = ocrService.recognize(imageBase64);
            if (text.isEmpty()) {
                return Result.error("识别失败，未检测到文字");
            }
            return Result.success(text);
        } catch (Exception e) {
            return Result.error("识别失败: " + e.getMessage());
        }
    }
}