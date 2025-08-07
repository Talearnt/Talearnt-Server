package com.talearnt.stomp.notification.response;


import com.talearnt.enums.stomp.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NotificationResDTO {

    private Long notificationNo; // 알림 번호
    private String senderNickname; // 알림을 보낸 사람의 닉네임
    private Long targetNo; // 게시글, 댓글 등 알림의 대상이 되는 엔티티의 번호
    private String content; // 알림 내용
    private NotificationType notificationType; // 알림 타입 (관심 키워드, 댓글 등)
    private Integer talentCodes; // 재능 코드 (관심 키워드 등에서 사용)
    private Boolean isRead; // 알림 읽음 여부
    private int unreadCount; // 읽지 않은 알림의 개수
    private LocalDateTime createdAt; // 알림 생성 시간

}
