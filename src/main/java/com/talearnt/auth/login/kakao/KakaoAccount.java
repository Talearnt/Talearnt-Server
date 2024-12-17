package com.talearnt.auth.login.kakao;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class KakaoAccount {

    @JsonProperty("email")
    private String email;

    //이름 제공 동의 여부
    @JsonProperty("name_needs_agreement")
    private Boolean isNameAgree;

    //카카오계정 이름
    @JsonProperty("name")
    private String name;

    //성별 제공 동의 여부
    @JsonProperty("gender_needs_agreement")
    private Boolean isGenderAgree;

    //성별
    @JsonProperty("gender")
    private String gender;

    //전화번호 제공 동의 여부
    @JsonProperty("phone_number_needs_agreement")
    private Boolean isPhoneNumberAgree;

    //전화번호
    //국내 번호인 경우 +82 00-0000-0000 형식
    @JsonProperty("phone_number")
    private String phoneNumber;
}
