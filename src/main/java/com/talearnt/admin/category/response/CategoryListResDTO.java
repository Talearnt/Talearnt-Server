package com.talearnt.admin.category.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@ToString
public class CategoryListResDTO {

    private Integer categoryCode; // 대분류 Code
    private String categoryName; // 대분류 이름
    private List<TalentCategoryResDTO> talents;

    @QueryProjection
    public CategoryListResDTO(Integer categoryCode, String categoryName, List<TalentCategoryResDTO> talents) {
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.talents = talents;
    }
}
