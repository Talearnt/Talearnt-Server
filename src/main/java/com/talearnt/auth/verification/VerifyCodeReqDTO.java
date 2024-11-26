package com.talearnt.auth.verification;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.enums.common.Regex;
import com.talearnt.util.valid.DynamicValid;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VerifyCodeReqDTO {
    @DynamicValid(errorCode = ErrorCode.USER_PHONE_NUMBER_FORMAT_MISMATCH, pattern = Regex.PHONE_NUMBER, notBlank = true)
    private String phone;
    private String inputCode;
}
