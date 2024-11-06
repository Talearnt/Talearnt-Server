package com.talearnt.login;

import com.talearnt.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class KakaoLoginResDTO {

    @Schema(example = "test@test.com")
    private String userId;

    @Schema(example = "01012345678")
    private String phone;

    @Schema(example = "남자/여자")
    private Gender gender;
}
