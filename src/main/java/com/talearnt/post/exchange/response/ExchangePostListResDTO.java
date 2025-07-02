package com.talearnt.post.exchange.response;

import com.querydsl.core.annotations.QueryProjection;
import com.talearnt.enums.post.ExchangePostStatus;
import com.talearnt.enums.post.ExchangeType;
import com.talearnt.enums.user.UserRole;
import com.talearnt.util.common.PostUtil;
import com.talearnt.util.common.SplitUtil;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@ToString
@NoArgsConstructor
public class  ExchangePostListResDTO {

    private String profileImg;
    private String nickname;
    private UserRole authority;

    private Long exchangePostNo;
    private ExchangePostStatus status;
    private ExchangeType exchangeType;
    private String duration;
    private boolean requiredBadge;

    private String title;
    private String content;
    private List<String> giveTalents;
    private List<String> receiveTalents;

    private LocalDateTime createdAt;
    private Long openedChatRoomCount;
    private Long count;
    private Long favoriteCount;
    private Boolean isFavorite;

    @Builder
    public ExchangePostListResDTO(String profileImg, String nickname, UserRole authority, Long exchangePostNo, ExchangePostStatus status, ExchangeType exchangeType, String duration, boolean requiredBadge, String title, String content, String giveTalents, String receiveTalents, LocalDateTime createdAt, Long count, Long openedChatRoomCount, Long favoriteCount, Boolean isFavorite) {
        this.profileImg = profileImg;
        this.nickname = nickname;
        this.authority = authority;
        this.exchangePostNo = exchangePostNo;
        this.status = status;
        this.exchangeType = exchangeType;
        this.duration = duration;
        this.requiredBadge = requiredBadge;
        this.title = title;
        this.content = content;
        this.giveTalents = SplitUtil.splitStringToList(giveTalents);
        this.receiveTalents = SplitUtil.splitStringToList(receiveTalents);
        this.createdAt = createdAt;
        this.count = count;
        this.openedChatRoomCount = openedChatRoomCount;
        this.favoriteCount = favoriteCount;
        this.isFavorite = isFavorite;
    }
}
