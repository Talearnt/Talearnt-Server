package com.talearnt.util.jwt;

import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TokenResDTO {
    private String accessToken;
}
