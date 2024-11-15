package com.talearnt.login.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.talearnt.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoLoginResDTO {

    @Schema(example = "test@test.com")
    private String userId;

    @Schema(example = "01012345678")
    private String phone;

    @Schema(example = "남자/여자")
    private Gender gender;
}
