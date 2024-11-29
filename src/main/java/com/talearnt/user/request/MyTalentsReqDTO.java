package com.talearnt.user.request;

import com.talearnt.util.common.RequiredJwtValueDTO;
import com.talearnt.util.jwt.UserInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@RequiredJwtValueDTO
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
public class MyTalentsReqDTO {
    @Schema(hidden = true)
    private UserInfo userInfo;

    private Integer talentCode;

    private Boolean type;
}
