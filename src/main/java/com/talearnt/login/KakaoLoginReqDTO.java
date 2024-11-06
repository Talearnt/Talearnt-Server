package com.talearnt.login;

import com.talearnt.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class KakaoLoginReqDTO {

    //카카오에서 넘어오는 코드
    private String authorizationCode;

}
