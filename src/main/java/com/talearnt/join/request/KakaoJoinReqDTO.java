package com.talearnt.join.request;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.enums.user.Gender;
import com.talearnt.enums.common.Regex;
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
public class KakaoJoinReqDTO {
    @Schema(required = true, example = "test@test.com")
    @DynamicValid(errorCode = ErrorCode.USER_ID_NOT_EMAIL_FORMAT, pattern = Regex.EMAIL, notBlank = true)
    private String userId;

    @Schema(example = "홍길동")
    @DynamicValid(errorCode = ErrorCode.USER_NAME_MISMATCH,pattern = Regex.NAME)
    private String name;

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
