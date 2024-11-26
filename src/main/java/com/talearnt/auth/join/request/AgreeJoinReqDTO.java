package com.talearnt.auth.join.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AgreeJoinReqDTO {
    @Schema(hidden = true)
    private Long userNo;
    @Schema(example = "1")
    private Long agreeCodeId;
    @Schema(example = "true")
    private boolean agree;
}
