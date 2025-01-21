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
public class ExchangePostDetailResDTO {
    //유저 관령
    private Long userNo; //채팅방 연결 및 자기 게시글 검증
    private String nickname;
    private String profileImg;
    private UserRole authority; //인증 유저 판단 ==> Boolean이 아닌 이유, 관리자일 경우 보여줄 뱃지도 다르게 보여주고 싶을 때가 있을 수도 있기 때문

    //게시글 관련
    private Long exchangePostNo; //채팅방 연결
    private List<String> giveTalents;
    private List<String> receiveTalents;
    private ExchangeType exchangeType;
    private ExchangePostStatus status;
    private LocalDateTime createdAt;
    private String duration;
    private Boolean requiredBadge;

    private String title;
    private String content;
    private List<String> images;
    private int count; //조회수
    private Long favoriteCount; //찜(좋아요)수
    private Long openedChatRoomCount; // 진행중인 채팅 방 수
    private Long chatRoomNo; //채팅방 접속할 ID
}
