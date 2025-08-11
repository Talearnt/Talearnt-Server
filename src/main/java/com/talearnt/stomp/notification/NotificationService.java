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
import com.talearnt.stomp.notification.repository.NotificationRepository;
import com.talearnt.stomp.notification.response.NotificationResDTO;
import com.talearnt.user.talent.repository.MyTalentQueryRepository;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.log.LogRunningTime;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Log4j2
@RequiredArgsConstructor
public class NotificationService {

    private final JdbcTemplate jdbcTemplate;
    private final SimpMessagingTemplate template;
    private final NotificationRepository notificationRepository;
    private final CommentQueryRepository commentQueryRepository;
    private final ReplyQueryRepository replyQueryRepository;
    private final ExchangePostQueryRepository exchangePostQueryRepository;
    private final MyTalentQueryRepository myTalentQueryRepository;


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
            Notification notification = NotificationMapper.INSTANCE.toNotificationFromExchangePost(user.getUserNo(), userReceiveTalents.getUserNo(),
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
        //알림 로그 저장
        Notification notification = NotificationMapper.INSTANCE.toNotificationFromComment(notificationInfo, notificationType);
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
