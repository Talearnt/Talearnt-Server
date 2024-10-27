package com.talearnt.verification;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VerificationReqDTO {
    private String userId;
    private String phone;
    private String verificationCode;
    private Boolean isPhoneVerified;
}
