package com.talearnt.join;

import com.talearnt.enums.Gender;
import com.talearnt.enums.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class JoinReqDTO {
    private String userId;
    private String pw;
    private String nickname;
    private Gender gender;
    private String phone;
    private String joinType;
    private UserRole authority;


}
