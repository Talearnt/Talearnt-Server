package com.talearnt.post.exchange.request;

import com.talearnt.enums.ErrorCode;
import com.talearnt.enums.Regex;
import com.talearnt.enums.post.ExchangeType;
import com.talearnt.post.exchange.PostTalentCategoryDTO;
import com.talearnt.util.common.RequiredJwtValueDTO;
import com.talearnt.util.jwt.UserInfo;
import com.talearnt.util.valid.DynamicValid;
import com.talearnt.util.valid.ListValid;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@RequiredJwtValueDTO
public class ExchangePostReqDTO {
    @Schema(hidden = true)
    private UserInfo userInfo;

    @ListValid(errorCode = ErrorCode.POST_REQUEST_MISSING, minLength = 1)
    @ListValid(errorCode = ErrorCode.POST_OVER_REQUEST_LENGTH, maxLength = 5)
    private List<PostTalentCategoryDTO> giveTalent;

    @ListValid(errorCode = ErrorCode.POST_REQUEST_MISSING, minLength = 1)
    @ListValid(errorCode = ErrorCode.POST_OVER_REQUEST_LENGTH, maxLength = 5)
    private List<PostTalentCategoryDTO> receiveTalent;

    @DynamicValid(errorCode = ErrorCode.POST_TITLE_OVER_LENGTH, maxLength = 30)
    @DynamicValid(errorCode = ErrorCode.POST_TITLE_MISSING, notBlank = true)
    private String title;

    @DynamicValid(errorCode = ErrorCode.POST_CONTENT_MIN_LENGTH, minLength = 20)
    @DynamicValid(errorCode = ErrorCode.POST_CONTENT_MISSING,notBlank = true)
    private String content;

    @DynamicValid(errorCode = ErrorCode.POST_BAD_REQUEST,pattern = Regex.EXCHANGE_TPYE
            , notBlank = true)
    private ExchangeType exchangeType;

    @DynamicValid(errorCode = ErrorCode.POST_BAD_REQUEST, notBlank = true)
    private boolean badgeRequired;

    @DynamicValid(errorCode = ErrorCode.POST_BAD_REQUEST,pattern = Regex.EXCHANGE_DURATION
            , notBlank = true)
    private String duration;
}
