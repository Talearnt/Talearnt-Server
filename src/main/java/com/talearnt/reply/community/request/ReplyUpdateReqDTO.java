package com.talearnt.reply.community.request;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.util.common.RequiredJwtValueDTO;
import com.talearnt.util.jwt.UserInfo;
import com.talearnt.util.valid.DynamicValid;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RequiredJwtValueDTO
public class ReplyUpdateReqDTO {
    @Schema(hidden = true)
    private UserInfo userInfo;

    @DynamicValid(errorCode = ErrorCode.COMMENT_CONTENT_OVER_LENGTH,  minLength = 3, maxLength = 300)
    private String content;
}
