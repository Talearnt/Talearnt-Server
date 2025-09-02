package com.talearnt.admin.notice.request;

import com.talearnt.enums.admin.notice.NoticeType;
import com.talearnt.enums.common.ErrorCode;
import com.talearnt.enums.common.Regex;
import com.talearnt.util.common.RequiredJwtValueDTO;
import com.talearnt.util.jwt.UserInfo;
import com.talearnt.util.valid.DynamicValid;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@RequiredJwtValueDTO
@ToString
public class NoticeInsertReqDTO {
    @Schema(hidden = true)
    private UserInfo userInfo;
    @DynamicValid(errorCode = ErrorCode.POST_TITLE_LENGTH_MISSING, minLength = 2,maxLength = 50)
    private String title;
    @DynamicValid(errorCode = ErrorCode.POST_CONTENT_MIN_LENGTH, minLength = 20)
    private String content;
    @DynamicValid(errorCode = ErrorCode.POST_BAD_REQUEST, pattern = Regex.NOTICE_TYPE)
    private NoticeType noticeType;
}
