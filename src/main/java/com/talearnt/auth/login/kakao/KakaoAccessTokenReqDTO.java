package com.talearnt.auth.login.kakao;

import com.google.auto.value.AutoValue.Builder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class KakaoAccessTokenReqDTO {
    private String kakaoAccessToken;
    private boolean autoLogin;
}
