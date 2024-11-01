package com.talearnt.post.exchange.request;

import com.talearnt.enums.post.ExchangeType;
import com.talearnt.post.exchange.PostTalentCategoryDTO;
import com.talearnt.util.common.RequiredJwtValueDTO;
import com.talearnt.util.jwt.UserInfo;
import lombok.*;

import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@RequiredJwtValueDTO
public class ExchangePostUpdateReqDTO {
    private long exchangePostNo;
    private UserInfo userInfo;
    private List<PostTalentCategoryDTO> giveTalent;
    private List<PostTalentCategoryDTO> receiveTalent;
    private String title;
    private String content;
    private ExchangeType exchangeType;
    private boolean badgeRequired;
    private String duration;
}
