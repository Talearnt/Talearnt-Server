package com.talearnt.admin.agree.request;

import com.talearnt.util.common.RequiredJwtValueDTO;
import com.talearnt.util.jwt.UserInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@RequiredJwtValueDTO
public class AgreeMarketingAndAdReqDTO {
    @Schema(hidden = true)
    private UserInfo userInfo;
    private boolean isAgree;
}
