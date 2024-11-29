package com.talearnt.admin.category.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@ToString
public class TalentCategoryResDTO {

    private Integer talentCode; // 재능 Code
    private String talentName; // 재능 이름

    @QueryProjection
    public TalentCategoryResDTO(Integer talentCode, String talentName) {
        this.talentCode = talentCode;
        this.talentName = talentName;
    }
}
