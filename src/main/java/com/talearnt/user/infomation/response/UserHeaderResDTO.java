package com.talearnt.user.infomation.response;


import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserHeaderResDTO {
    private Long userNo;
    private String profileImg;
    private String nickname;
    private boolean isKeywordSet;

    public boolean getIsKeywordSet() {
        return isKeywordSet;
    }
}
