package com.talearnt.join;

import com.talearnt.enums.Gender;
import com.talearnt.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class JoinReqDTO {

    @Schema(required = true, example = "example@example.com")
    private String userId;

    @Schema(required = true, description = "8자 이상, 숫자,문자, 특수기호 각 1개 이상 포함")
    private String pw;


    @Schema(required = true, example = "남자/여자")
    private Gender gender;

    @Schema(required = true, example = "01012345678")
    private String phone;

}
