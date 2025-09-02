package com.talearnt.post.exchange.request;


import com.talearnt.enums.common.ErrorCode;
import com.talearnt.enums.common.Regex;
import com.talearnt.enums.post.ExchangeType;
import com.talearnt.util.common.RequiredJwtValueDTO;
import com.talearnt.util.jwt.UserInfo;
import com.talearnt.util.valid.DynamicValid;
import com.talearnt.util.valid.ListValid;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@RequiredJwtValueDTO
public class ExchangePostReqDTO {

    @Schema(hidden = true)
    private UserInfo userInfo;
    @DynamicValid(errorCode = ErrorCode.POST_TITLE_LENGTH_MISSING, minLength = 2,maxLength = 50)
    private String title;
    @DynamicValid(errorCode = ErrorCode.POST_CONTENT_MIN_LENGTH, minLength = 20)
    private String content;
    @ListValid(errorCode = ErrorCode.POST_KEYWORD_LENGTH_MISSING, minLength = 1)
    @ListValid(errorCode = ErrorCode.POST_KEYWORD_LENGTH_OVER, maxLength = 5)
    private List<Integer> giveTalents;
    @ListValid(errorCode = ErrorCode.POST_KEYWORD_LENGTH_MISSING, minLength = 1)
    @ListValid(errorCode = ErrorCode.POST_KEYWORD_LENGTH_OVER, maxLength = 5)
    private List<Integer> receiveTalents;

    @Valid
    @DynamicValid(errorCode = ErrorCode.POST_BAD_REQUEST, pattern = Regex.EXCHANGE_TYPE)
    private ExchangeType exchangeType;

    private boolean requiredBadge;
    @DynamicValid(errorCode = ErrorCode.POST_DURATION_MISSING, pattern = Regex.EXCHANGE_DURATION)
    private String duration;

    private List<String> imageUrls; //S3에서 삭제할 파일 경로 PresignedURL 옵션 제거 경로
}
