package com.talearnt.post.community.request;

import com.talearnt.enums.post.PostType;
import com.talearnt.util.common.PostUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Pageable;


@Getter
@NoArgsConstructor
@ToString
public class CommunityPostSearchCondition {
    private PostType postType;
    private String order;
    private String path;
    private Long lastNo;
    private Pageable page;

    @Builder
    public CommunityPostSearchCondition(String postType, String order, String path, String lastNo, String page, String size) {
        this.postType = PostUtil.filterValidPostType(postType);
        this.order = PostUtil.filterValidOrderValue(order);
        this.path = PostUtil.filterValidPath(path);
        this.lastNo = PostUtil.parseLong(lastNo);
        this.page = PostUtil.filterValidPagination(page,size);
    }
}
