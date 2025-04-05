package com.talearnt.reply.community.request;

import com.talearnt.util.common.PostUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;


@Getter
@NoArgsConstructor
public class ReplySearchCondition {
    private Pageable page;
    private Long lastNo;

    @Builder
    public ReplySearchCondition(String lastNo, String page, String size) {
        this.page = PostUtil.filterValidPagination(page,size);
        this.lastNo = PostUtil.parseLong(lastNo);
    }

}
