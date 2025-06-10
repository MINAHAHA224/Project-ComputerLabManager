package com.example.computerweb.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry
                .addResourceHandler("/avatars/**") // Đường dẫn URL mà client sẽ gọi
                .addResourceLocations("file:D:/computerweb_uploads/avatars/"); // Thư mục gốc trong classpath nơi chứa file

    }

}
