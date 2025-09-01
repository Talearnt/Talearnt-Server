package com.talearnt.admin.notice.request;

import com.talearnt.enums.admin.notice.NoticeType;
import com.talearnt.util.common.RequiredJwtValueDTO;
import com.talearnt.util.jwt.UserInfo;
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
    private String title;
    private String content;
    private NoticeType noticeType;
}
