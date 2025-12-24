package com.talearnt.configure;

import com.talearnt.util.resolver.ClientPathResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ForwardedHeaderFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 대해 CORS 허용
                .allowedOrigins(
                        "http://localhost:80",
                        "http://localhost:8080",
                        "http://localhost:5173",
                        "http://localhost:5555",
                        "http://api.talearnt.net",
                        "https://api.talearnt.net",
                        "http://talearnt.net",
                        "https://talearnt.net",
                        "https://www.talearnt.net",
                        "https://doe331l0de5w8.cloudfront.net" // FE TEST 개발계 주소
                ) // 허용할 도메인
                .allowedMethods("GET", "POST", "PUT", "PATCH","DELETE") // 허용할 HTTP 메서드
                .allowedHeaders("*") // 허용할 헤더
                .allowCredentials(true) // 쿠키를 포함한 자격 증명 허용
                .maxAge(36000); // 캐싱 시간 (초)
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new ClientPathResolver()); // ClientPathResolver 추가
    }

    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }

}