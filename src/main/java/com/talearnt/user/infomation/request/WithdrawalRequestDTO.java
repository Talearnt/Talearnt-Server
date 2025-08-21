package com.talearnt.user.infomation.request;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.util.valid.DynamicValid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class WithdrawalRequestDTO {
    
    @DynamicValid(errorCode = ErrorCode.WITHDRAWAL_REASON_EMPTY, minLength = 1)
    private List<String> withdrawalReasons; // 선택한 탈퇴 사유 텍스트들: ["서비스를 더 이상 이용하지 않아요", "오류가 너무 자주 발생해요"]

    @DynamicValid(errorCode = ErrorCode.WITHDRAWAL_REASON_DETAIL_LENGTH_OVER, maxLength = 500)
    private String detailedReason;
}
