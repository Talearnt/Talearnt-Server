package com.talearnt.util.resolver;

import com.talearnt.enums.ErrorCode;
import com.talearnt.util.common.RequiredJwtValueDTO;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.jwt.UserInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;

import java.lang.reflect.Field;

@Log4j2
@RestControllerAdvice
public class RequestDtoArgumentResolver {
    @InitBinder
    public void initBinder(WebDataBinder binder, NativeWebRequest webRequest) {

        if (binder.getTarget() != null && binder.getTarget().getClass().isAnnotationPresent(RequiredJwtValueDTO.class)) {
            Object principal = webRequest.getUserPrincipal();
            // UserInfo 타입으로 변환
            UserInfo userInfo = null;
            if (principal instanceof UsernamePasswordAuthenticationToken) {
                Object principalObj = ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
                if (principalObj instanceof UserInfo) {
                    userInfo = (UserInfo) principalObj;
                } else {
                    throw new CustomRuntimeException(ErrorCode.INVALID_TOKEN);
                }
            }

            if (userInfo == null){
                log.error(ErrorCode.EXPIRED_TOKEN);
                throw new CustomRuntimeException(ErrorCode.EXPIRED_TOKEN);
            }

            if (userInfo != null) {

                try {
                    Field field = binder.getTarget().getClass().getDeclaredField("userInfo");
                    field.setAccessible(true);
                    field.set(binder.getTarget(), userInfo);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new CustomRuntimeException(ErrorCode.INVALID_TOKEN);
                }
            }

        }
    }
}
