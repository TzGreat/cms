package com.example.demo.service;

/**
 * OCR服务接口
 */
public interface OcrService {

    /**
     * Base64图片识别
     */
    String recognize(String imageBase64);

    /**
     * 字节数组图片识别
     */
    String recognize(byte[] imageBytes);
}