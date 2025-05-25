package com.talearnt.util.resolver;

import com.talearnt.enums.common.ClientPathType;
import com.talearnt.util.common.ClientPath;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class ClientPathResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(ClientPath.class) != null &&
                parameter.getParameterType().equals(ClientPathType.class);
    }

    @Override
    public Object resolveArgument(@NotNull MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
            throws Exception {
        String headerValue = webRequest.getHeader("X-Client-Path");
        if (headerValue == null) headerValue = "web"; // 기본값 설정

        return ClientPathType.from(headerValue); // enum으로 변환, 유효하지 않으면 예외 발생
    }
}
