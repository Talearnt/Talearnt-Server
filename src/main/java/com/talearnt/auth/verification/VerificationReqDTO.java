package com.talearnt.auth.verification;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.enums.common.Regex;
import com.talearnt.util.valid.DynamicValid;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VerificationReqDTO {

    @Schema(required = true, example = "01022223333")
    @DynamicValid(errorCode = ErrorCode.USER_PHONE_NUMBER_FORMAT_MISMATCH, pattern = Regex.PHONE_NUMBER, notBlank = true)
    private String phone;

    @DynamicValid(errorCode = ErrorCode.USER_ID_NOT_EMAIL_FORMAT,pattern = Regex.EMAIL)
    private String userId;

}
