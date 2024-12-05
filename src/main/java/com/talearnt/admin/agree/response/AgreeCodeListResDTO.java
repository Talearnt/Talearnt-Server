package com.talearnt.admin.agree.response;


import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Getter
@Builder
@ToString
@NoArgsConstructor
public class AgreeCodeListResDTO {
    private long agreeCodeId;
    private String title;
    private String content;
    private String version;
    private boolean mandatory;


    @QueryProjection
    public AgreeCodeListResDTO(long agreeCodeId, String title, String content, String version, boolean mandatory) {
        this.agreeCodeId = agreeCodeId;
        this.title = title;
        this.version = version;
        this.mandatory = mandatory;
        this.content = content;
    }
}
