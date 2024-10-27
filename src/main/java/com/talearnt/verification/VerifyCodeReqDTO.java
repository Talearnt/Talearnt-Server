package com.talearnt.verification;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VerifyCodeReqDTO {
    private String userId;
    private String inputCode;
}
