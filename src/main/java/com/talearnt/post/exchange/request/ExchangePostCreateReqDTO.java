package com.talearnt.post.exchange.request;

import com.talearnt.enums.post.ExchangeType;
import com.talearnt.post.exchange.PostTalentCategoryDTO;
import com.talearnt.util.common.RequiredJwtValueDTO;
import com.talearnt.util.jwt.UserInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@RequiredJwtValueDTO
public class ExchangePostCreateReqDTO {
    @Schema(hidden = true)
    private UserInfo userInfo;
    private List<PostTalentCategoryDTO> giveTalent;
    private List<PostTalentCategoryDTO> receiveTalent;
    private String title;
    private String content;
    private ExchangeType exchangeType;
    private boolean badgeRequired;
    private String duration;
}
