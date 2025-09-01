package com.talearnt.admin.notice.response;

import com.talearnt.enums.admin.notice.NoticeType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NoticeDetailResDTO {
    private Long noticeNo;
    private String title;
    private String content;
    private NoticeType noticeType;
    private LocalDateTime createdAt;
}
