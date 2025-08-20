package com.talearnt.stomp.firebase.request;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.util.common.RequiredJwtValueDTO;
import com.talearnt.util.jwt.UserInfo;
import com.talearnt.util.valid.DynamicValid;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@RequiredJwtValueDTO
public class FcmTokenReqDTO {
    
    @Schema(hidden = true)
    private UserInfo userInfo;
    
    @DynamicValid(errorCode = ErrorCode.BAD_PARAMETER, notBlank = true)
    @Schema(description = "FCM 토큰", example = "fcm_token_example_123", required = true)
    private String fcmToken;
    
    @Schema(description = "디바이스 식별자", example = "device_123", required = false)
    private String deviceIdentifier;
    
    @Schema(description = "디바이스 정보", example = "iPhone 14, iOS 17.0", required = false)
    private String deviceInfo;
}
