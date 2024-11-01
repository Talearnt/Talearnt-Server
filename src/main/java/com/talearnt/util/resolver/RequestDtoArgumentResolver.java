package com.talearnt.util.resolver;

import com.talearnt.util.common.RequiredJwtValueDTO;
import com.talearnt.util.jwt.UserInfo;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.Field;

public class RequestDtoArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // DTO 어노테이션 단 것들만 추적
        return parameter.getParameterType().isAnnotationPresent(RequiredJwtValueDTO.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        // DTO 인스턴스 생성
        Object dto = parameter.getParameterType().getDeclaredConstructor().newInstance();

        UserInfo userInfo = (UserInfo) webRequest.getUserPrincipal();

        //JWT가 Null이 아니라면
        if (userInfo != null) {
            // DTO의 필드 중 "userId"가 있는 경우 CustomDetails의 userId 값 주입
            for (Field field : dto.getClass().getDeclaredFields()) {
                if (field.getName().equals("userInfo")) {
                    field.setAccessible(true);
                    field.set(dto, userInfo);
                }
            }
        }

        return dto;
    }


}
