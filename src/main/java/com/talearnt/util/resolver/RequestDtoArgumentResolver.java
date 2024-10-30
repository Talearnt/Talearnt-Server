package com.talearnt.util.resolver;

import com.talearnt.util.common.RequestDTO;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class RequestDtoArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // DTO 어노테이션 단 것들만 추적
        return parameter.getParameterType().isAnnotationPresent(RequestDTO.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        // DTO 인스턴스 생성
        Object dto = parameter.getParameterType().getDeclaredConstructor().newInstance();

        // 현재 요청의 UserDetails를 가져옴
        /* User Details 정의하면 그때 주석 삭제 후 적용.
        CustomDetails userInfo = (CustomDetails) webRequest.getUserPrincipal();

        //JWT가 Null이 아니라면
        if (userInfo != null) {
            // DTO의 필드 중 "userId"가 있는 경우 CustomDetails의 userId 값 주입
            for (Field field : dtoInstance.getClass().getDeclaredFields()) {
                if (field.getName().equals("userId")) {
                    field.setAccessible(true);
                    field.set(dto, userInfo.getUserId());
                }
            }
        }

         */

        return dto;
    }


}
