package com.semo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Value("${com.semo.file.upload-dir}")
  private String uploadDir;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // 업로드된 파일을 /uploads/** URL로 접근 가능하도록 설정
    registry.addResourceHandler("/uploads/**")
        .addResourceLocations("file:" + uploadDir + "/");
  }
}