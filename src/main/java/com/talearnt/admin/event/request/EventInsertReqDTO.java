package com.talearnt.admin.event.request;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.enums.common.Regex;
import com.talearnt.util.common.RequiredJwtValueDTO;
import com.talearnt.util.jwt.UserInfo;
import com.talearnt.util.valid.DynamicValid;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@RequiredJwtValueDTO
public class EventInsertReqDTO {
    @Schema(hidden = true)
    private UserInfo userInfo;

    @DynamicValid(errorCode= ErrorCode.POST_REQUEST_MISSING, notBlank = true)
    private String content;

    @DynamicValid(errorCode = ErrorCode.FILE_UPLOAD_EXTENSION_MISMATCH, pattern = Regex.FILE_EXTENSION)
    @DynamicValid(errorCode = ErrorCode.POST_REQUEST_MISSING, notBlank = true)
    private String bannerUrl;

    @DynamicValid(errorCode= ErrorCode.POST_REQUEST_MISSING, notBlank = true)
    private LocalDateTime startDate;

    private LocalDateTime endDate;
}
