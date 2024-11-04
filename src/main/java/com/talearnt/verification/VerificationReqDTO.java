package com.talearnt.verification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VerificationReqDTO {

    @Schema(required = true, example = "example@example.com")
    private String userId;

    @Schema(required = true, example = "01012345678")
    private String phone;

    @Schema(description = "서버에서 4자리 인증번호 생성")
    private String verificationCode;

    @Schema(description = "default = false, 인증 후 true")
    private Boolean isPhoneVerified;
}
