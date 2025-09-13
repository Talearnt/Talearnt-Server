package com.talearnt.admin.agree.request;

import com.talearnt.util.jwt.UserInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AgreeCodeMarketingReqDTO {
    @Schema(hidden = true)
    private UserInfo userInfo;
    private boolean isMarketingAgree;
}
