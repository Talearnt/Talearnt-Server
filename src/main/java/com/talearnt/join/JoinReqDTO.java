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

    @Schema(description = "랜덤 닉네임 자동 주입")
    private String nickname;

    @Schema(required = true, example = "남자/여자")
    private Gender gender;

    @Schema(required = true, example = "01012345678")
    private String phone;

    @Schema(description = "회원가입 경로에 따라 자동 주입 자사/카카오")
    private String joinType;

    @Schema(description = "ROLE_USER 기본값 자동 주입")
    private UserRole authority;
}
