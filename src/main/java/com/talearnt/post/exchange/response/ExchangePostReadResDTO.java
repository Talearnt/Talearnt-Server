package com.talearnt.post.exchange.response;

import com.talearnt.enums.UserRole;
import com.talearnt.enums.post.ExchangeType;
import com.talearnt.post.exchange.PostTalentCategoryDTO;
import lombok.*;

import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ExchangePostReadResDTO {
    //게시글 번호
    private long exchangePostNo;
    //이하 유저 관련
    private long userNo;
    private String userId;
    private String nickname;
    private String profileImg;
    private UserRole authority;

    //이하 게시글 관련
    private List<PostTalentCategoryDTO> giveTalent;
    private List<PostTalentCategoryDTO> receiveTalent;
    private String title;
    private String content;
    private ExchangeType exchangeType;
    private boolean badgeRequired;
    private String duration;
}
