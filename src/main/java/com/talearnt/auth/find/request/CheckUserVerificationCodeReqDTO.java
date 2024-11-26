package com.talearnt.auth.find.request;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.enums.common.Regex;
import com.talearnt.util.valid.DynamicValid;
import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CheckUserVerificationCodeReqDTO {
    @DynamicValid(errorCode = ErrorCode.BAD_PARAMETER,pattern = Regex.SMS_TYPE)
    private String type;
    @DynamicValid(errorCode = ErrorCode.USER_PHONE_NUMBER_FORMAT_MISMATCH, pattern = Regex.PHONE_NUMBER)
    private String phone;
    @DynamicValid(errorCode = ErrorCode.AUTH_CODE_FORMAT_MISMATCH, pattern = Regex.AUTH_CODE)
    private String code;
}
