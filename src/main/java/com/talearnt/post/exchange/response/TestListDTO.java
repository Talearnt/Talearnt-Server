package com.talearnt.post.exchange.response;

import lombok.*;

import java.util.List;


@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TestListDTO {
    private Long exchangePostNo;
    private List<String> giveTalents;
    private List<String> receiveTalents;
}
