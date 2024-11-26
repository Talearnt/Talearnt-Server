package com.talearnt.auth.find.request;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.enums.common.Regex;
import com.talearnt.util.valid.DynamicValid;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class FindByPhoneReqDTO {
    @DynamicValid(errorCode = ErrorCode.BAD_PARAMETER,pattern = Regex.SMS_TYPE)
    private String type;
    @DynamicValid(errorCode = ErrorCode.USER_PHONE_NUMBER_FORMAT_MISMATCH,pattern = Regex.PHONE_NUMBER)
    private String phone;
}
