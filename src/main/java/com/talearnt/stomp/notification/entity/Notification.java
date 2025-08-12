package com.talearnt.stomp.notification.entity;

import com.talearnt.enums.stomp.NotificationType;
import com.talearnt.util.converter.notification.IntegerListConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_seq")
    @SequenceGenerator(name = "notification_seq", sequenceName = "notification_sequence", initialValue = 1, allocationSize = 50)
    @GenericGenerator(
            name = "notification_seq",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "notification_sequence"),
                    @Parameter(name = "optimizer", value = "none")
            }
    )
    private Long notificationNo; // 알림 번호
    private Long senderNo; // 알림을 보낸 사람의 번호
    private Long receiverNo; // 알림을 받는 사람의 번호
    private Long targetNo; // 게시글, 댓글 등 알림의 대상이 되는 엔티티의 번호
    private String content; // 알림 내용

    @Convert(converter = IntegerListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<Integer> talentCodes; // 재능 코드 (관심 키워드 등에서 사용)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType; // 알림 타입 (관심 키워드, 댓글 등)
    private Boolean isRead; // 알림 읽음 여부
    private int unreadCount; // 읽지 않은 알림의 개수

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt; // 알림 생성 시간
}
