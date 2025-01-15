package com.talearnt.post.exchange.response;

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
@AllArgsConstructor
public class ExchangePostResDTO {
    //유저 관령
    private Long userNo; //채팅방 연결 및 자기 게시글 검증
    private String nickname;
    private String profileImg;
    private UserRole authority;

    //게시글 관련
    private Long exchangePostNo; //채팅방 연결
    private List<String> giveTalents;
    private List<String> receiveTalents;
    private ExchangeType exchangeType;
    private ExchangePostStatus status;
    private LocalDateTime createdAt;
    private String duration;
    private boolean requiredBadge;

    private String title;
    private String content;
    private int count;
    private int favoriteCount;
    private int openedChatRoomCount;
    private Long chatRoomNo; //채팅방 접속할 ID
}
