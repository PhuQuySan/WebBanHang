package com.asm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.asm.AuthInterceptor;

@Configuration
public class AuthConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Đăng ký AuthInterceptor cho các URL cần kiểm tra
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/admin/**", "/account/**") // Kiểm tra với các URL này
                .excludePathPatterns("/auth/login", "/login/register", "/static/**"); // Bỏ qua kiểm tra với URL này
    }
}