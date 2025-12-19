package com.example.todo_app;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 1. 允許所有的 API 路徑
                .allowedOrigins("http://localhost:5173") // 2. 允許來自 Vue 開發伺服器的請求 (Vite 預設是 5173)
                .allowedMethods("GET", "POST", "PUT", "DELETE") // 3. 允許這些 HTTP 動作
                .allowedHeaders("*");
    }
}