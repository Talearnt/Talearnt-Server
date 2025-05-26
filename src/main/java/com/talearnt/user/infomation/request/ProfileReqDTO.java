package com.talearnt.user.infomation.request;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.enums.common.Regex;
import com.talearnt.util.common.RequiredJwtValueDTO;
import com.talearnt.util.jwt.UserInfo;
import com.talearnt.util.valid.DynamicValid;
import com.talearnt.util.valid.ListValid;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RequiredJwtValueDTO
public class ProfileReqDTO {
    @Schema(hidden = true)
    private UserInfo userInfo;

    @DynamicValid(errorCode = ErrorCode.USER_NICKNAME_MISMATCH, pattern = Regex.NICKNAME)
    private String nickname;

    private String profileImg;

    @ListValid(errorCode = ErrorCode.POST_REQUEST_MISSING, minLength = 1)
    @ListValid(errorCode = ErrorCode.POST_KEYWORD_LENGTH_MISSING, maxLength = 5)
    private List<Integer> giveTalents;

    @ListValid(errorCode = ErrorCode.POST_REQUEST_MISSING, minLength = 1)
    @ListValid(errorCode = ErrorCode.POST_KEYWORD_LENGTH_MISSING, maxLength = 5)
    private List<Integer> receiveTalents;
}
