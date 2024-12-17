package com.talearnt.auth.login.kakao;

import com.talearnt.enums.user.Gender;
import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class KakaoLoginResDTO {
    private boolean isRegistered;
    private String accessToken;
    private String userId;
    private String name;
    private String phone;
    private Gender gender;

    public KakaoLoginResDTO(boolean isRequiredRedirect, String accessToken) {
        this.isRegistered = isRequiredRedirect;
        this.accessToken = accessToken;
    }

    public KakaoLoginResDTO(boolean isRequiredRedirect, String userId, String name, String phone, String gender) {
        this.isRegistered = isRequiredRedirect;
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
}
