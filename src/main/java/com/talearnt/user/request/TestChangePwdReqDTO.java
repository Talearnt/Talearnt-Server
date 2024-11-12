package com.talearnt.user.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TestChangePwdReqDTO {
    @Schema(example = "test@test.com")
    private String userId;
    @Schema(example = "!1q2w3e4r")
    private String pw;
}
