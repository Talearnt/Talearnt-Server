package com.talearnt.auth.login.kakao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.talearnt.enums.user.Gender;
import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class KakaoLoginResDTO {
    @JsonProperty("isRegistered")
    private boolean isRegistered;
    private String accessToken;
    private String userId;
    private String name;
    private String phone;
    private Gender gender;

    public KakaoLoginResDTO(boolean isRegistered, String accessToken) {
        this.isRegistered = isRegistered;
        this.accessToken = accessToken;
    }

    public KakaoLoginResDTO(boolean isRegistered, String userId, String name, String phone, String gender) {
        this.isRegistered = isRegistered;
        this.userId = userId;
        this.name = name;
        if (phone != null){
            this.phone = phone.replace("+82","0").replaceAll("[^0-9]", "");
        }
        if (gender == null){
            this.gender = null;
        }else{
            this.gender = Gender.fromString(gender);
        }

    }

    // registered 필드를 무시
    @JsonIgnore
    public boolean getRegistered() {
        return isRegistered;
    }

}
