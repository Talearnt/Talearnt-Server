package com.talearnt.post.community.request;

import com.talearnt.enums.post.PostType;
import com.talearnt.util.common.PostUtil;
import lombok.*;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
@ToString
public class CommunityPostSearchConditionDTO {
    private PostType postType;
    private String order;
    private String path;
    private Long lastNo;
    private Double popularScore;
    private LocalDateTime baseTime;
    private Pageable page;

    @Builder
    public CommunityPostSearchConditionDTO(String postType, String order, String path, String lastNo, String popularScore, String baseTime, String page, String size) {
        this.postType = PostUtil.filterValidPostType(postType);
        this.order = PostUtil.filterValidOrderValue(order);
        this.path = PostUtil.filterValidPath(path);
        this.lastNo = PostUtil.parseLong(lastNo);
        this.popularScore = PostUtil.parseDouble(popularScore);
        this.baseTime = PostUtil.filterValidBaseTime(baseTime);
        this.page = PostUtil.filterValidPagination(page,size);
    }
}
