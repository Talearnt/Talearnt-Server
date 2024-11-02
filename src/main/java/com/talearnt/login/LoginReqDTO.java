package com.talearnt.login;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.stereotype.Component;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginReqDTO {

    @Schema(example = "test@test.com")
    private String userId;
    @Schema(example = "test")
    private String pw;

}
