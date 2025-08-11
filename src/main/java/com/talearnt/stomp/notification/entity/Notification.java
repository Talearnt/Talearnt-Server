package com.talearnt.stomp.notification.entity;

import com.talearnt.enums.stomp.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationNo; // 알림 번호
    private Long senderNo; // 알림을 보낸 사람의 번호
    private Long receiverNo; // 알림을 받는 사람의 번호
    private Long targetNo; // 게시글, 댓글 등 알림의 대상이 되는 엔티티의 번호
    private String content; // 알림 내용

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType; // 알림 타입 (관심 키워드, 댓글 등)
    private Boolean isRead; // 알림 읽음 여부


    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 알림 생성 시간
    private LocalDateTime updatedAt; // 변경 시간
    private LocalDateTime deletedAt; // 알림 삭제 시간 (null == 삭제되지 않음)
}
