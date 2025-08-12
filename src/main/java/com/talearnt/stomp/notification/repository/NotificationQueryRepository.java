package com.talearnt.stomp.notification.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import com.talearnt.enums.stomp.NotificationType;
import com.talearnt.stomp.notification.entity.Notification;
import com.talearnt.stomp.notification.entity.QNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NotificationQueryRepository {

    private final JPAQueryFactory factory;
    private final QNotification notification = QNotification.notification;

    public Optional<Notification> findByNotificationTypeAndTargetNoAndReceiverNo(
            NotificationType notificationType, Long targetNo, Long receiverNo) {
        //특정 알림 타입, 대상 번호, 수신자 번호에 해당하는 알림을 조회합니다.
        return Optional.ofNullable(factory
                .selectFrom(notification)
                .where(notification.notificationType.eq(notificationType) // 알림 타입
                        .and(notification.targetNo.eq(targetNo)) // 게시글 또는 댓글 번호
                        .and(notification.senderNo.eq(receiverNo))) // 수신자 번호
                .fetchOne());
    }

}
