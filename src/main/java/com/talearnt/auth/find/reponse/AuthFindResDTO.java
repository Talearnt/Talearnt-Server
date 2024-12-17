package com.talearnt.auth.find.reponse;

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
public class AuthFindResDTO {
    private String userId;
    private LocalDateTime sentDate;

    @QueryProjection
    public AuthFindResDTO(String userId, LocalDateTime sentDate) {
        this.userId = userId;
        this.sentDate = sentDate;
    }
}
