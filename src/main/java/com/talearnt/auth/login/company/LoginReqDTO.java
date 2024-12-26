package com.talearnt.auth.login.company;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.enums.common.Regex;
import com.talearnt.util.valid.DynamicValid;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginReqDTO {

    @Schema(example = "test@test.com")
    @DynamicValid(errorCode = ErrorCode.USER_ID_NOT_EMAIL_FORMAT, pattern = Regex.EMAIL,notBlank = true)
    private String userId;
    @Schema(example = "!1q2w3e4r")
    @DynamicValid(errorCode = ErrorCode.USER_PASSWORD_PATTERN_MISMATCH, pattern = Regex.PASSWORD)
    @DynamicValid(errorCode = ErrorCode.USER_PASSWORD_MISSING, minLength = 8)
    private String pw;

    private boolean autoLogin;
}
