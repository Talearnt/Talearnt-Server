package com.talearnt.login.company;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginReqDTO {

    @Schema(example = "test@test.com")
    private String userId;
    @Schema(example = "!1q2w3e4r")
    private String pw;

}
