package com.shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${uploadPath}") //appication.properties에 설정한 uploadPath 읽어오기
    String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/images/**") // url이 images로 시작하는 경우 uploadPath에 설정한 폴더를 기준으로 파일을 읽어오도록 설정
                .addResourceLocations(uploadPath); // root 경로 설정
    }
}
