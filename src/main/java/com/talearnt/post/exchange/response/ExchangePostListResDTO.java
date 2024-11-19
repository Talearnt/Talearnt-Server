package com.talearnt.post.exchange.response;

import com.querydsl.core.annotations.QueryProjection;
import com.talearnt.enums.user.UserRole;
import com.talearnt.enums.post.ExchangePostStatus;
import com.talearnt.post.exchange.PostTalentCategoryDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
public class ExchangePostListResDTO {
    private long exchangePostNo;
    private String nickname;
    private UserRole authority;
    private List<PostTalentCategoryDTO> giveTalent;
    private List<PostTalentCategoryDTO> receiveTalent;
    private String title;
    private String content;
    private int count;
    private ExchangePostStatus status;
    //찜 게시글 수
    private LocalDateTime createdAt;


    @QueryProjection
    public ExchangePostListResDTO(long exchangePostNo, String nickname, UserRole authority,
                                  List<PostTalentCategoryDTO> giveTalent, List<PostTalentCategoryDTO> receiveTalent,
                                  String title, String content, int count,
                                  ExchangePostStatus status, LocalDateTime createdAt) {
        this.exchangePostNo = exchangePostNo;
        this.nickname = nickname;
        this.authority = authority;
        this.giveTalent = giveTalent;
        this.receiveTalent = receiveTalent;
        this.title = title;
        this.content = content;
        this.count = count;
        this.status = status;
        this.createdAt = createdAt;
    }

}
