package com.talearnt.post.exchange.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.util.List;



@Getter
@ToString
@NoArgsConstructor
public class TestListDTO {
    private List<String> giveTalents;
    private List<String> receiveTalents;

    @QueryProjection
    @Builder
    public TestListDTO(String giveTalents, String receiveTalents) {
        this.giveTalents = giveTalents != null ? List.of(giveTalents.split(",")) : List.of();
        this.receiveTalents = receiveTalents != null ? List.of(receiveTalents.split(",")) : List.of();;
    }
}
