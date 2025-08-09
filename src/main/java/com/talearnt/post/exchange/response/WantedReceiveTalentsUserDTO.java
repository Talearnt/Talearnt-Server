package com.talearnt.post.exchange.response;

import com.talearnt.util.common.SplitUtil;
import lombok.*;

import java.util.List;


@Builder
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WantedReceiveTalentsUserDTO {
    private Long userNo; // 유저 번호
    private String userId; // 유저 아이디
    private List<Integer> receiveTalentNos; // 재능 PK 리스트를 문자열로 저장

    public WantedReceiveTalentsUserDTO(Long userNo, String userId, String receiveTalentNos) {
        this.userNo = userNo;
        this.userId = userId;
        this.receiveTalentNos = SplitUtil.splitToIntegerList(receiveTalentNos);
    }
}
