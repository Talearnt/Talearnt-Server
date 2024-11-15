package com.talearnt.util.common;

import com.talearnt.enums.ErrorCode;
import com.talearnt.user.entity.User;
import com.talearnt.util.exception.CustomRuntimeException;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class LoginUtil {

    /**가입 방식이 자사, 네이버, 카카오톡인지 판단, 각 로그인 비즈니스 로직에 있어야 합니다.*/
    public static void validateJoinType(User user, String joinType){
        if (!user.getJoinType().equals(joinType)){
            log.info("{} 로그인 서비스 실패 - 해당 유저는 가입 방식이 {}이 아님: {}", joinType,joinType,ErrorCode.AUTH_METHOD_CONFLICT);
            throw new CustomRuntimeException(ErrorCode.AUTH_METHOD_CONFLICT);
        }
    }
}
