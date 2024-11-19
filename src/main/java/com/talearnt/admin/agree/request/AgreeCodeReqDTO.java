package com.talearnt.admin.agree.request;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.enums.common.Regex;
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
public class AgreeCodeReqDTO {
    @Schema(hidden = true)
    private UserInfo userInfo;

    @DynamicValid(errorCode = ErrorCode.TERMS_TITLE_MISSING, minLength = 2, notBlank = true)
    @Schema(example = "개인 정보 이용 동의 약관")
    private String title;

    @DynamicValid(errorCode = ErrorCode.TERMS_INVALID_VERSION,pattern = Regex.VERSION,notBlank = true)
    @Schema(example = "1.0")
    private String version;

    @DynamicValid(errorCode = ErrorCode.TERMS_CONTENT_MISSING, minLength = 15, notBlank = true)
    @Schema(example = "개인 정보 이용 동의 해주면 정말로 감사하겠습니다. 오늘도 좋은 하루~")
    private String content;

    @Schema(example = "true")
    private boolean mandatory;
}
