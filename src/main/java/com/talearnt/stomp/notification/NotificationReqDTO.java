package com.talearnt.stomp.notification;

import com.talearnt.enums.stomp.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NotificationReqDTO {
    private Long notificationNo; // 알림 번호
    private Long senderNo; // 알림을 보낸 사람의 번호
    private Long receiverNo; // 알림을 받는 사람의 번호
    private Long targetNo; // 게시글, 댓글 등 알림의 대상이 되는 엔티티의 번호
    private String content; // 알림 내용
    private NotificationType notificationType; // 알림 타입 (관심 키워드, 댓글 등)
    private String url; // 알림 클릭 시 이동할 URL
    private int unreadCount; // 읽지 않은 알림의 개수
}
