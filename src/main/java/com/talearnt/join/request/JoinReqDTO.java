package com.talearnt.join.request;

import com.talearnt.enums.ErrorCode;
import com.talearnt.enums.Gender;
import com.talearnt.enums.Regex;
import com.talearnt.util.valid.DynamicValid;
import com.talearnt.util.valid.ListValid;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class JoinReqDTO {

    @Schema(required = true, example = "test@test.com")
    @DynamicValid(errorCode = ErrorCode.USER_ID_NOT_EMAIL_FORMAT, pattern = Regex.EMAIL, notBlank = true)
    private String userId;

    @Schema(required = true, example = "!1q2w3e4r", description = "8자 이상, 숫자,문자, 특수기호 각 1개 이상 포함")
    @DynamicValid(errorCode = ErrorCode.USER_PASSWORD_PATTERN_MISMATCH,pattern = Regex.PASSWROD)
    @DynamicValid(errorCode = ErrorCode.USER_PASSWORD_MISSING,notBlank = true)
    private String pw;

    @Schema(required = true, example = "남자")
    @DynamicValid(errorCode = ErrorCode.USER_GENDER_MISSMATCH,pattern = Regex.GENDER)
    private Gender gender;

    @Schema(required = true, example = "01029089421")
    @DynamicValid(errorCode = ErrorCode.USER_PHONE_NUMBER_FORMAT_MISMATCH, pattern = Regex.PHONE_NUMBER, notBlank = true)
    private String phone;

    //FE에서 넘어오는 이용 약관
    @ListValid(errorCode = ErrorCode.USER_NOTHING_AGREE, minLength = 1)
    private List<AgreeJoinReqDTO> agreeReqDTOS;
}
