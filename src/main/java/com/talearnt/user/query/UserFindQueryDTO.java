package com.talearnt.user.query;

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
public class UserFindQueryDTO {
    private String userId;
    private UserRole authority;

    @QueryProjection
    public UserFindQueryDTO(String userId, UserRole authority) {
        this.userId = userId;
        this.authority = authority;
    }
}