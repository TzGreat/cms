package com.example.demo.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfig {
    
    @Value("${langchain4j.dashscope.api-key}")
    private String apiKey;
    
    @Value("${langchain4j.dashscope.model-name:qwen-plus}")
    private String modelName;
    
    @Value("${langchain4j.dashscope.temperature:0.7}")
    private double temperature;
    
    @Value("${langchain4j.dashscope.max-tokens:1000}")
    private int maxTokens;
    
    @Bean
    public ChatLanguageModel chatLanguageModel() {
        // 使用OpenAI兼容模式连接通义千问
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .modelName(modelName)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .build();
    }
}