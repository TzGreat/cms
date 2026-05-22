package com.example.demo.service.impl;

import com.example.demo.service.OcrService;
import com.example.demo.utils.BaiduOcrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class OcrServiceImpl implements OcrService {

    @Autowired
    private BaiduOcrUtil baiduOcrUtil;

    @Override
    public String recognize(String imageBase64) {
        // 移除Base64头部
        String cleanBase64 = imageBase64.replaceAll("^data:image/[a-zA-Z]+;base64,", "");
        // 解码为字节数组
        byte[] imageBytes = Base64.getDecoder().decode(cleanBase64);
        return baiduOcrUtil.recognizeText(imageBytes);
    }

    @Override
    public String recognize(byte[] imageBytes) {
        return baiduOcrUtil.recognizeText(imageBytes);
    }
}