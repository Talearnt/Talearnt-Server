package com.talearnt.util.common;

import com.talearnt.enums.ErrorCode;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.jwt.UserInfo;

public class UserUtil {

    /**
     * 회원 정보를 검증하는 객체입니다.
     * UserInfo의 객체 검증이 필요한 곳에서 사용하면 됩니다.
     * @param userInfo JWT 토큰 안에 있는 유저의 정보입니다.
     * */
    public static void validateUserInfo(UserInfo userInfo) {
        if (userInfo == null || userInfo.getUserId() == null || userInfo.getUserId() == null) {
            throw new CustomRuntimeException(ErrorCode.INVALID_TOKEN);
        }
    }
}
