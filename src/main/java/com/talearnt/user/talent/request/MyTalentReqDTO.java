package com.talearnt.user.talent.request;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.util.common.RequiredJwtValueDTO;
import com.talearnt.util.jwt.UserInfo;
import com.talearnt.util.valid.ListValid;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@RequiredJwtValueDTO
public class MyTalentReqDTO {

    @Schema(hidden = true)
    private UserInfo userInfo;

    @ListValid(errorCode = ErrorCode.POST_REQUEST_MISSING, minLength = 1)
    @ListValid(errorCode = ErrorCode.POST_KEYWORD_LENGTH_MISSING, maxLength = 5)
    private List<Integer> giveTalents;

    @ListValid(errorCode = ErrorCode.POST_REQUEST_MISSING, minLength = 1)
    @ListValid(errorCode = ErrorCode.POST_KEYWORD_LENGTH_MISSING, maxLength = 5)
    private List<Integer> receiveTalents;

}
