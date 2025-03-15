package com.talearnt.comment.community.request;

import com.talearnt.util.common.PostUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Pageable;

@Getter
@ToString
@NoArgsConstructor
public class CommentSearchCondition {

    private Long lastNo;
    private Pageable page;

    @Builder
    public CommentSearchCondition(String lastNo, String page, String size) {
        this.lastNo = PostUtil.parseLong(lastNo);
        this.page = PostUtil.filterValidPagination(page, size);
    }
}
