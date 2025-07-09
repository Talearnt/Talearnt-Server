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
public class NoticeListToWebResDTO extends NoticeListResDTO {
    private String content;

    public NoticeListToWebResDTO(Long noticeNo, String title, NoticeType noticeType, LocalDateTime createdAt, String content) {
        super(noticeNo, title, noticeType, createdAt);
        this.content = content;
    }

}
