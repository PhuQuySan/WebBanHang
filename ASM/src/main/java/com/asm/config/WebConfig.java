package com.asm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ánh xạ URL "/uploads/**" tới thư mục "Uploads" bên ngoài dự án
        registry.addResourceHandler("/Uploads/**")
                .addResourceLocations("file:Uploads/"); // Thay "Uploads/" bằng đường dẫn thư mục thực tế
    }
}
