package com.talearnt.post.exchange;

import com.talearnt.enums.post.ExchangeType;
import com.talearnt.util.common.RequestDTO;
import com.talearnt.util.jwt.UserInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@RequestDTO
public class ExchangePostCreateReqDTO {
    @Schema(hidden = true)
    private UserInfo userInfo; // 이거 내일 실험
    private List<PostTalentCategoryDTO> giveTalent;
    private List<PostTalentCategoryDTO> receiveTalent;
    private String title;
    private String content;
    private ExchangeType exchangeType;
    private boolean badgeRequired;
    private String duration;
}
