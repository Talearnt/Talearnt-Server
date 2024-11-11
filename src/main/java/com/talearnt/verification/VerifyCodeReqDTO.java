package com.talearnt.verification;

import com.talearnt.enums.ErrorCode;
import com.talearnt.enums.Regex;
import com.talearnt.util.valid.DynamicValid;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VerifyCodeReqDTO {
    @DynamicValid(errorCode = ErrorCode.USER_ID_NOT_EMAIL_FORMAT, pattern = Regex.EMAIL, notBlank = true)
    private String userId;
    private String inputCode;
}
