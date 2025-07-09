package com.talearnt.admin.notice.response;

import com.talearnt.enums.admin.notice.NoticeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;


@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeListResDTO {
    private Long noticeNo;
    private String title;
    private NoticeType noticeType;
    private LocalDateTime createdAt;
}
