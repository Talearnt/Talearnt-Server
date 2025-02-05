package com.talearnt.user.infomation.response;


import lombok.*;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserHeaderResDTO {
    private Long userNo;
    private String profileImg;
    private String nickname;
    private List<Integer> giveTalentCodes;
}
