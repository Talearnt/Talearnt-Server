package com.talearnt.post.exchange.response;


import com.talearnt.util.common.PostUtil;
import com.talearnt.util.common.SplitUtil;
import lombok.*;

import java.util.List;

@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
/* Exchange Post 의 주고 싶은 재능이
 * 유저의 내가 받고 싶은 재능과 일치할 때 알림을 보내기 위한 DTO
 * 이 receiveTalentNos는 ExchangePost의 주고 싶은 재능이 들어갑니다.
 * */
public class ExchangeReceiveTalentDTO {
    private Long userNo; // 유저 번호
    private String userId; // 유저 아이디
    private List<Integer> receiveTalentNos; // 재능 PK 리스트

    public ExchangeReceiveTalentDTO(Long userNo, String userId, String receiveTalentNos) {
        this.userNo = userNo;
        this.userId = userId;
        this.receiveTalentNos = SplitUtil.splitToIntegerList(receiveTalentNos);
    }

}
