package com.ecomweb.order_service.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // config thu muc chua anh
    // không lưu ảnh trong resource vì khi buil file jar/war thư mục bị gói trong đó và không thể lưu ảnh và path đó
    // mac dinh springboot chi phuc vu file trong resource, config nhu nay de no lay duoc anh tu thu muc chi dinh
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/");
    }
}