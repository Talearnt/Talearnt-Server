package com.talearnt.user.talent.request;

import com.talearnt.util.common.RequiredJwtValueDTO;
import lombok.*;


@RequiredJwtValueDTO
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
public class MyTalentCodesReqDTO {
    private Integer talentCode;
}
