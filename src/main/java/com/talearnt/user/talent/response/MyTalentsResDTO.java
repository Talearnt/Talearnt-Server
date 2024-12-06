package com.talearnt.user.talent.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

@Builder
@Getter
@ToString
@NoArgsConstructor
public class MyTalentsResDTO {
    private Integer talentCode;
    private String talentName;

    @QueryProjection
    public MyTalentsResDTO(Integer talentCode, String talentName) {
        this.talentCode = talentCode;
        this.talentName = talentName;
    }
}
