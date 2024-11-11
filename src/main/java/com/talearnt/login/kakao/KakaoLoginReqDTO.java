package com.talearnt.login.kakao;

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
