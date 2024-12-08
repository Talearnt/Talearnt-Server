package com.talearnt.auth.login.kakao;

import com.talearnt.enums.user.Gender;
import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class KakaoLoginResDTO {
    private boolean isRequiredRedirect;
    private String accessToken;
    private String userId;
    private String phone;
    private Gender gender;

    public KakaoLoginResDTO(boolean isRequiredRedirect, String accessToken) {
        this.isRequiredRedirect = isRequiredRedirect;
        this.accessToken = accessToken;
    }

    public KakaoLoginResDTO(boolean isRequiredRedirect, String userId, String phone, String gender) {
        this.isRequiredRedirect = isRequiredRedirect;
        this.userId = userId;
        this.phone = phone;
        if (gender == null){
            this.gender = null;
        }else{
            this.gender = Gender.fromString(gender);
        }

    }
}
