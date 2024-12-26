package com.talearnt.post.exchange.response;

import com.querydsl.core.annotations.QueryProjection;
import com.talearnt.enums.post.ExchangePostStatus;
import com.talearnt.enums.post.ExchangeType;
import com.talearnt.enums.user.UserRole;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@ToString
@NoArgsConstructor
public class ExchangePostListResDTO {

    private String profileImg;
    private String nickname;
    private UserRole authority;

    private ExchangePostStatus status;
    private ExchangeType exchangeType;
    private String duration;
    private boolean requiredBadge;

    private String title;
    private String content;
    private List<String> giveTalents;
    private List<String> receiveTalents;

    private LocalDateTime createdAt;
    private int count;
    private int favoriteCount;

    @QueryProjection
    public ExchangePostListResDTO(String profileImg, String nickname, UserRole authority, ExchangePostStatus status, ExchangeType exchangeType, String duration, boolean requiredBadge, String title, String content, List<String> giveTalents, List<String> receiveTalents, LocalDateTime createdAt, int count, int favoriteCount) {
        this.profileImg = profileImg;
        this.nickname = nickname;
        this.authority = authority;
        this.status = status;
        this.exchangeType = exchangeType;
        this.duration = duration;
        this.requiredBadge = requiredBadge;
        this.title = title;
        this.content = content;
        this.giveTalents = giveTalents;
        this.receiveTalents = receiveTalents;
        this.createdAt = createdAt;
        this.count = count;
        this.favoriteCount = favoriteCount;
    }
}
