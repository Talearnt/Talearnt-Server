package com.talearnt.post.exchange.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.util.List;

@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TestListDTO {
    private List<String> giveTalents;
    private List<String> receiveTalents;

}
