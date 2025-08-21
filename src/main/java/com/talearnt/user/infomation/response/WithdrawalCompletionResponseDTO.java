package com.talearnt.user.infomation.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalCompletionResponseDTO {
    private String userId;
    private LocalDateTime withdrawnAt;
}
