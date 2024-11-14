package com.talearnt.user.reponse;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@NoArgsConstructor
public class UserFindIdResDTO {
    private String userId;
    private LocalDateTime createdAt;

    @QueryProjection
    public UserFindIdResDTO(String userId, LocalDateTime createdAt) {
        this.userId = userId;
        this.createdAt = createdAt;
    }
}
