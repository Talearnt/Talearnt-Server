package com.talearnt.login;

import lombok.*;
import org.springframework.stereotype.Component;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginReqDTO {

    private String userId;
    private String pw;

}
