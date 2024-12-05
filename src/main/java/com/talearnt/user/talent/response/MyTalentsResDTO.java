package com.talearnt.user.talent.response;

import lombok.*;

@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MyTalentsResDTO {
    private Integer talentCode;
    private String talentName;
}
