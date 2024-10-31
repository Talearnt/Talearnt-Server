package com.talearnt.post.exchange;

import com.talearnt.enums.post.ExchangeType;
import com.talearnt.util.common.RequestDTO;
import lombok.*;

import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@RequestDTO
public class ExchangePostCreateReqDTO {
    private String userId;
    private List<PostTalentCategoryDTO> giveTalent;
    private List<PostTalentCategoryDTO> receiveTalent;
    private String title;
    private String content;
    private ExchangeType exchangeType;
    private boolean badgeRequired;
    private String duration;
}
