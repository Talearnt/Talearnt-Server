package com.talearnt.configure;

import com.talearnt.util.resolver.RequestDtoArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

public class WebConfig implements WebMvcConfigurer {



    @Override
    /**Request DTO에 JWT 값이 자동 주입 될 수 있도록하는 설정*/
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new RequestDtoArgumentResolver());
    }
}
