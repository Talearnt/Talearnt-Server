package com.talearnt.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Builder
@Component
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginReqDTO {

    private String userId;
    private String pw;

}
