package com.talearnt.verification;

import com.talearnt.enums.ErrorCode;
import com.talearnt.enums.Regex;
import com.talearnt.util.valid.DynamicValid;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VerificationReqDTO {

    @Schema(required = true, example = "test@test.com")
    @DynamicValid(errorCode = ErrorCode.USER_ID_NOT_EMAIL_FORMAT, pattern = Regex.EMAIL, notBlank = true)
    private String userId;

    @Schema(required = true, example = "01022223333")
    @DynamicValid(errorCode = ErrorCode.USER_PHONE_NUMBER_FORMAT_MISMATCH, pattern = Regex.PHONE_NUMBER, notBlank = true)
    private String phone;

    @Schema(hidden = true)
    private String verificationCode;

    @Schema(hidden = true)
    private Boolean isPhoneVerified;
}
