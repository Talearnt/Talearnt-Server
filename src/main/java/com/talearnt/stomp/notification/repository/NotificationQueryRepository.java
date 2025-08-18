package com.talearnt.stomp.notification.repository;


import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.talearnt.enums.stomp.NotificationType;
import com.talearnt.stomp.notification.entity.Notification;
import com.talearnt.stomp.notification.entity.NotificationSetting;
import com.talearnt.stomp.notification.entity.QNotification;
import com.talearnt.stomp.notification.entity.QNotificationSetting;
import com.talearnt.stomp.notification.response.NotificationResDTO;
import com.talearnt.stomp.notification.response.NotificationSettingResDTO;
import com.talearnt.user.infomation.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NotificationQueryRepository {

    private final JPAQueryFactory factory;
    private final QNotification notification = QNotification.notification;
    private final QUser user = QUser.user;
    private final QNotificationSetting notificationSetting = QNotificationSetting.notificationSetting;


    /**
     * 특정 사용자의 알림 설정을 조회합니다.
     *
     * @param userNo 사용자 번호
     * @return Optional<NotificationSettingResDTO> 해당 사용자의 알림 설정이 존재하면 Optional에 담아 반환, 없으면 빈 Optional 반환
     */
    public Optional<NotificationSettingResDTO> getNotificationSettings(Long userNo) {
        // 특정 사용자의 알림 설정을 조회합니다.
        return Optional.ofNullable(
                factory.select(Projections.constructor(NotificationSettingResDTO.class,
                                notificationSetting.allowCommentNotifications, // 댓글 알림 허용 여부
                                notificationSetting.allowKeywordNotifications // 관심 키워드 알림 허용 여부
                        ))
                        .from(notificationSetting)
                        .where(notificationSetting.user.userNo.eq(userNo)) // 사용자 번호로 필터링
                        .fetchOne() // 결과를 단일 객체로 반환
        );
    }


    /**
     * 특정 사용자의 알림을 조회합니다.
     *
     * @param userNo 사용자 번호
     * @return List<NotificationResDTO> 해당 사용자의 알림 목록
     */
    public List<NotificationResDTO> getNotifications(Long userNo){

        return factory.select(Projections.constructor(NotificationResDTO.class,
                notification.notificationNo, // 알림 번호
                user.nickname, // 알림을 보낸 사람의 닉네임
                notification.targetNo, // 게시글, 댓글 등 알림의 대상이 되는 엔티티의 번호
                notification.content, // 알림 내용
                notification.notificationType, // 알림 타입 (관심 키워드, 댓글 등)
                notification.talentCodes, // 재능 코드 (관심 키워드 등에서 사용
                notification.isRead, // 알림 읽음 여부
                notification.unreadCount, // 읽지 않은 알림의 개수
                notification.createdAt)
                )
                .from(notification)
                .leftJoin(user).on(notification.senderNo.eq(user.userNo)) // 알림을 보낸 사람의 정보 조인
                .where(notification.receiverNo.eq(userNo)) // 수신자 번호가 일치하는 알림만 조회
                .orderBy(notification.createdAt.desc()) // 최신순으로 정렬
                .limit(50) // 최대 50개 알림 조회
                .fetch(); // 결과를 리스트로 반환
    }



    /**
     * 특정 알림 타입, 대상 번호, 수신자 번호에 해당하는 알림을 조회합니다.
     *
     * @param notificationType 알림 타입
     * @param targetNo         게시글 또는 댓글 번호
     * @param receiverNo       수신자 번호
     * @return Optional<Notification> 해당 알림이 존재하면 Optional에 담아 반환, 없으면 빈 Optional 반환
     */
    public Optional<Notification> findByNotificationTypeAndTargetNoAndReceiverNo(
            NotificationType notificationType, Long targetNo, Long receiverNo) {
        //특정 알림 타입, 대상 번호, 수신자 번호에 해당하는 알림을 조회합니다.
        return Optional.ofNullable(factory
                .selectFrom(notification)
                .where(notification.notificationType.eq(notificationType) // 알림 타입
                        .and(notification.targetNo.eq(targetNo)) // 게시글 또는 댓글 번호
                        .and(notification.receiverNo.eq(receiverNo))) // 수신자 번호
                .fetchOne());
    }


    /**
     * 특정 사용자의 알림 설정을 조회합니다.
     *
     * @param receiverNo 사용자 번호
     * @return Optional<NotificationSetting> 해당 사용자의 알림 설정이 존재하면 Optional에 담아 반환, 없으면 빈 Optional 반환
     */
    public Optional<NotificationSetting> findNotificationSettingByReceiverNo(Long receiverNo) {
        // 특정 사용자의 알림 설정을 조회합니다.
        return Optional.ofNullable(factory
                .selectFrom(notificationSetting)
                .where(notificationSetting.user.userNo.eq(receiverNo)) // 사용자 번호로 필터링
                .fetchOne());
    }

}
