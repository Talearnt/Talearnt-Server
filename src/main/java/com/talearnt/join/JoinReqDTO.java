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
    @Schema(required = true, example = "example.com")
    private String userId;
    private String pw;
    @Schema(description = "닉네임을 정제하여 넣어주세요.")
    private String nickname;
    private Gender gender;
    private String phone;
    private String joinType;
    private UserRole authority;
}
