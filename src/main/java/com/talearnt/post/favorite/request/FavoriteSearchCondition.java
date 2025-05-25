package com.talearnt.post.favorite.request;

import com.talearnt.util.common.PostUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

@Getter
@NoArgsConstructor
public class FavoriteSearchCondition {
    private Pageable page;

    @Builder
    public FavoriteSearchCondition(String page, String size) {
        this.page = PostUtil.filterValidPagination(page, size);
    }
}
