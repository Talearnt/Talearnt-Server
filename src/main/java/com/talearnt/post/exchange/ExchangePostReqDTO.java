package com.talearnt.post.exchange;

import com.talearnt.enums.post.ExchangeType;
import com.talearnt.post.exchange.entity.TalentCategory;
import com.talearnt.util.common.RequestDTO;
import lombok.*;

import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@RequestDTO
public class ExchangeAddReqDTO {
    private long id;
    private String userId;
    private List<TalentCategory> giveTalent;
    private List<TalentCategory> receiveTalent;
    private String title;
    private String content;
    private ExchangeType exchangeType;
    private boolean badgeRequired;
    private String endDate;
}
