package com.talearnt.stomp.notification.entity;

import com.talearnt.user.infomation.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationSettingNo; // 알림 설정 ID

    // 어떤 유저의 알림 설정인지 매핑
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", nullable = false, unique = true)
    private User user;

    // 댓글 알림 허용 여부
    @Column(nullable = false)
    private boolean allowCommentNotifications = true;

    // 관심 키워드 알림 허용 여부
    @Column(nullable = false)
    private boolean allowKeywordNotifications = true;
}
