package com.talearnt.stomp.notification;

import com.talearnt.comment.community.repository.CommentQueryRepository;
import com.talearnt.comment.community.response.CommentNotificationDTO;
import com.talearnt.enums.common.ErrorCode;
import com.talearnt.enums.stomp.NotificationType;
import com.talearnt.post.exchange.repository.ExchangePostQueryRepository;
import com.talearnt.post.exchange.response.ExchangeReceiveTalentDTO;
import com.talearnt.post.exchange.response.WantedReceiveTalentsUserDTO;
import com.talearnt.reply.community.repository.ReplyQueryRepository;
import com.talearnt.stomp.notification.entity.Notification;
import com.talearnt.stomp.notification.entity.NotificationSetting;
import com.talearnt.stomp.notification.repository.NotificationQueryRepository;
import com.talearnt.stomp.notification.repository.NotificationRepository;
import com.talearnt.stomp.notification.repository.NotificationSettingRepository;
import com.talearnt.stomp.notification.response.NotificationResDTO;
import com.talearnt.stomp.notification.response.NotificationSettingResDTO;
import com.talearnt.user.infomation.entity.User;
import com.talearnt.user.talent.repository.MyTalentQueryRepository;
import com.talearnt.util.common.UserUtil;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.jwt.UserInfo;
import com.talearnt.util.log.LogRunningTime;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Log4j2
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate template;
    private final NotificationRepository notificationRepository;
    private final CommentQueryRepository commentQueryRepository;
    private final ReplyQueryRepository replyQueryRepository;
    private final ExchangePostQueryRepository exchangePostQueryRepository;
    private final MyTalentQueryRepository myTalentQueryRepository;
    private final NotificationQueryRepository notificationQueryRepository;
    private final NotificationSettingRepository notificationSettingRepository;


    public NotificationSettingResDTO getNotificationSettings(Authentication authentication) {
        log.info("알림 설정 조회 시작");

        UserInfo userInfo = UserUtil.validateAuthentication("알림 설정 조회", authentication);

        NotificationSettingResDTO notificationSettingResDTO = notificationQueryRepository.getNotificationSettings(userInfo.getUserNo())
                .orElseGet(()->{
                    User user = new User();
                    user.setUserNo(userInfo.getUserNo());

                    //알림 설정이 없으면 기본값으로 생성
                    NotificationSetting notificationSetting = new NotificationSetting();
                    notificationSetting.setUser(user);
                    notificationSetting.setAllowCommentNotifications(true); // 기본적으로 댓글 알림 허용
                    notificationSetting.setAllowKeywordNotifications(true); // 기본적으로 키워드 알림 허용

                    //알림 설정 저장
                    NotificationSetting savedSetting = notificationSettingRepository.save(notificationSetting);

                    //DTO로 변환하여 반환
                    return NotificationSettingResDTO.builder()
                            .allowKeywordNotifications(true)
                            .allowCommentNotifications(true)
                            .build();
                });

        log.info("알림 설정 조회 완료: {}", notificationSettingResDTO);
        return notificationSettingResDTO;
    }


    /**
     * 현재 사용자의 알림을 조회합니다.
     * 최대 50개만 조회합니다.
     * @param authentication 인증 정보
     * @return 알림 리스트
     */
    @LogRunningTime
    public List<NotificationResDTO> getNotifications(Authentication authentication) {
        log.info("알림 조회 시작");
        UserInfo userInfo = UserUtil.validateAuthentication("알림 조회", authentication);

        //사용자의 알림을 조회
        List<NotificationResDTO> notifications = notificationQueryRepository.getNotifications(userInfo.getUserNo());

        log.info("알림 조회 완료: {}개 알림", notifications.size());
        return notifications;
    }


    /**
     * 알림을 삭제합니다.
     * @param notificationNo 삭제할 알림 번호 리스트
     * @param authentication 인증 정보
     */
    @LogRunningTime
    @Transactional
    public void readNotification(List<Long> notificationNo, Authentication authentication) {
        log.info("알림 읽음 처리 시작: {}", notificationNo);

        UserInfo userInfo = UserUtil.validateAuthentication("알림 읽음 처리", authentication);

        //알림 번호로 알림을 조회하고 읽음 상태로 업데이트
        List<Notification> notifications = notificationRepository.findAllById(notificationNo);
        if (notifications.isEmpty()) {
            log.warn("읽음 처리할 알림이 없습니다. 알림 번호: {}", notificationNo);
            return;
        }

        notifications.forEach(notification -> {
            //알림이 존재하지 않거나, 알림의 수신자가 현재 사용자와 일치하지 않는 경우 예외 처리
            if (!notification.getReceiverNo().equals(userInfo.getUserNo())) {
                log.error("알림에 대한 권한이 없습니다. 알림 번호: {}, 알림 주인 사용자: {}, 현재 사용자: {}",
                        notification.getNotificationNo(), notification.getReceiverNo(), userInfo.getUserNo());
                throw new CustomRuntimeException(ErrorCode.NOTIFICATION_ACCESS_DENIED);
            }

            notification.setIsRead(true); // 알림을 읽음 상태로 변경
            notification.setUnreadCount(0); // 읽음 상태로 변경 시 읽지 않은 개수 초기화
        });

        //알림 저장
        notificationRepository.saveAll(notifications);
        log.info("알림 읽음 처리 완료: {}", notificationNo);
    }

    /**
     * 알림을 삭제합니다.
     * @param notificationNo 삭제할 알림 번호 리스트
     * @param authentication 인증 정보
     */
    @LogRunningTime
    @Transactional
    public void deleteNotification(List<Long> notificationNo, Authentication authentication) {
        log.info("알림 삭제 시작: {}", notificationNo);

        UserInfo userInfo = UserUtil.validateAuthentication("알림 삭제", authentication);

        //알림 번호로 알림을 조회
        List<Notification> notifications = notificationRepository.findAllById(notificationNo);
        if (notifications.isEmpty()) {
            log.warn("삭제할 알림이 없습니다. 알림 번호: {}", notificationNo);
            return;
        }

        notifications.stream()
                .filter(notification -> !notification.getReceiverNo().equals(userInfo.getUserNo()))
                .findFirst()
                .ifPresent(notification -> {
                    log.error("알림에 대한 권한이 없습니다 - 알림 번호: {}, 알림 주인 사용자: {}, 현재 사용자: {}",
                            notification.getNotificationNo(), notification.getReceiverNo(), userInfo.getUserNo());
                    throw new CustomRuntimeException(ErrorCode.NOTIFICATION_ACCESS_DENIED);
                });

        //알림 삭제
        notificationRepository.deleteAll(notifications);
        log.info("알림 삭제 완료: {}", notificationNo);
    }



    @Async
    @Transactional
    @LogRunningTime
    public void sendNotificationForMatchedKeyword(Long postNo){
        //게시글의 주고 싶은 재능들을 추출합니다.
        ExchangeReceiveTalentDTO userReceiveTalents = exchangePostQueryRepository.getGiveTalentAndUserIdInExchangePostByPostNo(postNo)
                .orElseThrow(() -> {
                    log.error("알림을 보내기 위한 게시글을 조회할 수 없습니다. postNo: {} - {} ", postNo,ErrorCode.POST_NOT_FOUND);
                    return new CustomRuntimeException(ErrorCode.POST_NOT_FOUND);
                });

        //게시글의 주고 싶은 재능과 유저가 받고 싶은 재능이 일치한 사람들의 아이디와 재능 일치하는 재능 코드들을 가져옵니다.
        Set<WantedReceiveTalentsUserDTO> wantedReceiveTalentsUser = myTalentQueryRepository.getWantedReceiveTalentsUserByTalentCodes(
                userReceiveTalents.getUserNo(),
                userReceiveTalents.getReceiveTalentNos());
        log.info("알림을 보내기 위한 유저 정보: {}", wantedReceiveTalentsUser);

        //받고 싶은 재능이 일치하는 유저가 없으면 종료
        if (wantedReceiveTalentsUser.isEmpty()){
            return;
        }

        //벌크 인서트 전용 List 생성
        List<Notification> notifications = new ArrayList<>();

        //알림을 생성하고 전송합니다.
        for (WantedReceiveTalentsUserDTO user : wantedReceiveTalentsUser) {
            List<Integer> receiveTalentNos = userReceiveTalents.getReceiveTalentNos().stream().filter(
                    talentCode -> user.getReceiveTalentNos().contains(talentCode)
            ).toList();

            //알림 로그 저장
            Notification notification = NotificationMapper.INSTANCE.toNotificationFromExchangePost(userReceiveTalents.getUserNo(), user.getUserNo(),
                    postNo, receiveTalentNos,NotificationType.INTERESTING_KEYWORD);

            //알림 엔티티를 리스트에 추가
            notifications.add(notification);
        }

        //알림 저장
        List<Notification> savedNotifications = notificationRepository.saveAll(notifications);

        // 저장된 알림 엔티티로 실시간 알림 전송
        int i = 0;
        for (WantedReceiveTalentsUserDTO user : wantedReceiveTalentsUser) {
            Notification savedNotification = savedNotifications.get(i++);

            List<Integer> receiveTalentNos = userReceiveTalents.getReceiveTalentNos().stream().filter(
                    talentCode -> user.getReceiveTalentNos().contains(talentCode)
            ).toList();
            // 알림 DTO 변환
            NotificationResDTO notificationResDTO = NotificationMapper.INSTANCE.toNotificationResDTO(savedNotification, user.getReceiveTalentNos(), user.getUserId());

            // WebSocket으로 알림 전송
            template.convertAndSendToUser(user.getUserId(), "/queue/notifications", notificationResDTO);
        }


    }


    /**
     * 댓글에 달린 답글에 대해 댓글 작성자에게 알림을 전송합니다.
     * 동작 과정:
     * <ol>
     *   <li>replyNo로 답글을 조회합니다. 게시글, 댓글, 답글 중 하나라도 없으면 예외를 발생시킵니다.</li>
     *   <li>답글 작성자와 댓글 작성자가 동일하면 알림을 전송하지 않습니다.</li>
     *   <li>답글 정보를 Notification 엔티티로 변환하여 저장합니다.</li>
     *   <li>Notification 엔티티를 DTO로 변환합니다.</li>
     *   <li>댓글 작성자의 userId를 username으로 하여 WebSocket 알림을 전송합니다.</li>
     * </ol>
     *
     * <b>주의:</b>
     * <ul>
     *   <li>WebSocket 세션의 username은 로그인 시 사용하는 userId와 일치해야 합니다.</li>
     * </ul>
     *
     * @param replyNo 알림을 발생시킬 답글의 PK
     * @throws CustomRuntimeException 게시글, 댓글 또는 답글이 존재하지 않을 때 (REPLY_NOT_FOUND)
     */
    @Async
    @Transactional
    @LogRunningTime
    public void sendNotificationForMyCommentReply(Long replyNo){
        log.info("답글 알림 전송 시작 : {}", replyNo);

        CommentNotificationDTO replyNotification = replyQueryRepository.getReplyNotification(replyNo)
                .orElseThrow(() ->{
                    log.error("답글 알림을 위한 댓글 조회 실패 - 게시글, 댓글 또는 답글이 없음: {} - {}", replyNo, ErrorCode.REPLY_NOT_FOUND);
                    return new CustomRuntimeException(ErrorCode.REPLY_NOT_FOUND);
                });

        //답글 작성자와 댓글 작성자가 동일하면 알림을 전송하지 않음
        if(replyNotification.getSenderNo().equals(replyNotification.getReceiverNo())){
            log.info("답글 작성자와 댓글 작성자가 동일하여 알림을 전송하지 않음: {}", replyNotification);
            return;
        }

        //알림 생성(전송) 및 저장
        createAndSendNotification(replyNotification, NotificationType.REPLY);

    }



    /**
     * 게시글에 달린 댓글에 대해 게시글 작성자에게 알림을 전송합니다.
     * 동작 과정:
     * <ol>
     *   <li>commentNo로 댓글을 조회합니다. 없으면 예외를 발생시킵니다.</li>
     *   <li>댓글 작성자와 게시글 작성자가 동일하면 알림을 전송하지 않습니다.</li>
     *   <li>댓글 정보를 Notification 엔티티로 변환하여 저장합니다.</li>
     *   <li>Notification 엔티티를 DTO로 변환합니다.</li>
     *   <li>게시글 작성자의 userId를 username으로 하여 WebSocket 알림을 전송합니다.</li>
     * </ol>
     *
     * <b>주의:</b>
     * <ul>
     *   <li>WebSocket 세션의 username은 로그인 시 사용하는 userId와 일치해야 합니다.</li>
     *   <li>댓글, 게시글, 사용자 엔티티의 연관관계가 올바르게 설정되어 있어야 합니다.</li>
     * </ul>
     *
     * @param commentNo 알림을 발생시킬 댓글의 PK
     * @throws CustomRuntimeException 댓글이 존재하지 않을 때
     */
    @Async
    @Transactional
    @LogRunningTime
    public void sendNotificationForMyPostComment(Long commentNo){
        log.info("댓글 알림 전송 시작 : {}", commentNo);

        CommentNotificationDTO comment = commentQueryRepository.getCommentNotification(commentNo);

        //게시글 작성자와 보내는 사람의 이름이 같을 경우 그냥 종료
        if(comment.getSenderNo().equals(comment.getReceiverNo())){
            return;
        }

        //알림 생성(전송) 및 저장
        createAndSendNotification(comment, NotificationType.COMMENT);

    }

    /**
     * 알림을 생성하고 사용자에게 전송하는 공통 메소드
     *
     * @param notificationInfo 알림 정보가 담긴 DTO
     * @param notificationType 알림 유형(COMMENT, REPLY 등)
     */
    private void createAndSendNotification(CommentNotificationDTO notificationInfo, NotificationType notificationType) {
        log.info("알림 생성 시작 - 알림 정보: {}, 알림 타입: {}", notificationInfo, notificationType);

        //알림 받을 사람의 알림 설정을 체크 ==> 댓글,답글 확인 여부
        NotificationSetting notificationSetting = notificationQueryRepository
                .findNotificationSettingByReceiverNo(notificationInfo.getReceiverNo())
                .orElseGet(() -> {
                    User user = new User();
                    user.setUserNo(notificationInfo.getReceiverNo());

                    NotificationSetting setting = new NotificationSetting();
                    setting.setUser(user);
                    setting.setAllowCommentNotifications(true); // 기본적으로 댓글 알림 허용
                    setting.setAllowKeywordNotifications(true); // 기본적으로 키워드 알림 허용
                    return notificationSettingRepository.save(setting);
                });


        //알림 설정에서 댓글 알림이 허용되지 않은 경우 알림을 보내지 않음
        if (!notificationSetting.isAllowCommentNotifications()) {
            log.info("댓글 알림이 허용되지 않아 알림을 전송하지 않습니다 - 알림 정보: {}", notificationInfo);
            return;
        }

        //알림 로그 저장
        Notification notification = notificationQueryRepository
                .findByNotificationTypeAndTargetNoAndReceiverNo(notificationType, notificationInfo.getTargetNo(), notificationInfo.getReceiverNo()) // 있으면 기존 회원 알림 조회
                .orElse(NotificationMapper.INSTANCE.toNotificationFromComment(notificationInfo, notificationType));// 없으면 새로 생성

        log.info("알림 생성 또는 조회 완료 - 알림 정보: {}", notification);

        //기존 알림이라면 내용을 업데이트
        if (notification.getNotificationNo() != null) {
            log.info("기존 알림이 존재하여 업데이트 진행 - 알림 번호: {}", notification.getNotificationNo());
            notification.setSenderNo(notificationInfo.getSenderNo()); // 알림을 보낸 사람의 번호
            notification.setContent(notificationInfo.getContent());
            notification.setIsRead(false);
            notification.setCreatedAt(LocalDateTime.now()); // 생성 시간을 현재 시간으로 업데이트
        }

        //읽지 않은 개수 +1 (초기 값도 1로 셋팅)
        notification.setUnreadCount(notification.getUnreadCount() + 1);

        Notification savedNotification = notificationRepository.save(notification);

        //DTO로 변환
        NotificationResDTO notificationResDTO = NotificationMapper.INSTANCE
                .toNotificationResDTOFromCommentNotificationEntity(notification, notificationInfo.getSenderNickname());

        //해당 유저에게 알림 전송
        template.convertAndSendToUser(notificationInfo.getReceiverId(), "/queue/notifications", notificationResDTO);

        log.info("알림 전송 완료 - \n 받을 유저 : {}, \n 받는 정보 : {}, \n 구독 경로 : {}",
                notificationInfo.getReceiverId(),
                notificationResDTO,
                "/queue/notifications");
    }

}
