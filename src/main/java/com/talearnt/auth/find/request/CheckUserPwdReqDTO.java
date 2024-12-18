package com.talearnt.auth.find.request;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.enums.common.Regex;
import com.talearnt.util.valid.DynamicValid;
import lombok.*;

/** 유저의 패스워드를 체크하는 Request DTO*/
@Getter
@Builder
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class CheckUserPwdReqDTO {
    @DynamicValid(errorCode = ErrorCode.USER_PASSWORD_PATTERN_MISMATCH, pattern = Regex.PASSWORD)
    private String pw;
    @DynamicValid(errorCode = ErrorCode.USER_PASSWORD_PATTERN_MISMATCH, pattern = Regex.PASSWORD)
    private String checkedPw;
}
