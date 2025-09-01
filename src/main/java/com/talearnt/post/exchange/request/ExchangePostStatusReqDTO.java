package com.talearnt.post.exchange.request;

import com.talearnt.enums.post.ExchangePostStatus;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExchangePostStatusReqDTO {
    private ExchangePostStatus status;
}
