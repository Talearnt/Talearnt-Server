package com.talearnt.auth.login.kakao;


import lombok.*;

@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class KakaoAccessTokenReqDTO {
    private String kakaoAccessToken;
    private boolean autoLogin;
}
