package com.talearnt.comment.community.request;

import com.talearnt.util.common.PostUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@ToString
@NoArgsConstructor
public class CommentSearchCondition {

    private Long lastNo;
    private LocalDateTime deletedAt;
    private Pageable page;

    @Builder
    public CommentSearchCondition(String lastNo,String deletedAt, String page, String size) {
        this.lastNo = PostUtil.parseLong(lastNo);
        this.deletedAt = deletedAt != null? LocalDateTime.parse(deletedAt, DateTimeFormatter.ISO_DATE_TIME).plusHours(9) : null;
        this.page = PostUtil.filterValidPagination(page, size);
    }
}
