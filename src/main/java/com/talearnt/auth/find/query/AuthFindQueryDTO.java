package com.talearnt.auth.find.query;

import com.querydsl.core.annotations.QueryProjection;
import com.talearnt.enums.user.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@NoArgsConstructor
public class AuthFindQueryDTO {
    private String userId;
    private UserRole authority;

    @QueryProjection
    public AuthFindQueryDTO(String userId, UserRole authority) {
        this.userId = userId;
        this.authority = authority;
    }
}
