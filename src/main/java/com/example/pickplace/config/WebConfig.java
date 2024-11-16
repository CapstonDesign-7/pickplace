package com.example.pickplace.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // 윈도우 경로를 URL 형식으로 변환
        String uploadPath = uploadDir.replace("\\", "/");
        if (!uploadPath.endsWith("/")) {
            uploadPath += "/";
        }

        registry.addResourceHandler("/images/**")  // /images/** URL 패턴으로 접근
                .addResourceLocations("file:///" + uploadPath)  // 윈도우 경로를 file:/// 형식으로 변환
                .setCachePeriod(3600)
                .resourceChain(true);

        System.out.println("Resource location: file:///" + uploadPath); // 디버깅용
    }
}