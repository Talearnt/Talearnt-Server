package com.talearnt.user.talent.response;


import lombok.*;

import java.util.List;

/**
 * 나의 재능을 가져오기 위한 페이지입니다.
 * 마이페이지에서 사용할 예정입니다.
 * */


@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MyTalentsListResDTO {
    private List<MyTalentsResDTO> giveTalents;
    private List<MyTalentsResDTO> interestTalents;
}
