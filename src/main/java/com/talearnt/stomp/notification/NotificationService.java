package com.talearnt.stomp.notification;


import com.talearnt.comment.community.entity.CommunityComment;
import com.talearnt.comment.community.repository.CommentQueryRepository;
import com.talearnt.comment.community.repository.CommentRepository;
import com.talearnt.comment.community.response.CommentNotificationDTO;
import com.talearnt.enums.common.ErrorCode;
import com.talearnt.enums.stomp.NotificationType;
import com.talearnt.stomp.notification.entity.Notification;
import com.talearnt.stomp.notification.repository.NotificationRepository;
import com.talearnt.stomp.notification.response.NotificationResDTO;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.log.LogRunningTime;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;


@Service
@Log4j2
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate template;
    private final NotificationRepository notificationRepository;
    private final CommentRepository commentRepository;
    private final CommentQueryRepository commentQueryRepository;
    @Autowired
    private SimpUserRegistry simpUserRegistry; // 추가




    /**
     * 게시글에 달린 댓글에 대해 게시글 작성자에게 알림을 전송합니다.
     * <p>
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
    @Transactional
    @LogRunningTime
    public void sendNotificationForMyPostComment(Long commentNo){
        log.info("댓글 알림 전송 시작 : {}", commentNo);

        CommentNotificationDTO comment = commentQueryRepository.getCommentNotification(commentNo);

        //게시글 작성자와 보내는 사람의 이름이 같을 경우 그냥 종료
        if(comment.getSenderNo() == comment.getReceiverNo()){
            return;
        }

        //알림 로그 저장
        Notification notification = NotificationMapper.INSTANCE.toNotificationFromComment(comment, NotificationType.COMMENT);
        log.info("댓글 알림 매퍼 엔티티로 변환 : {}",notification);
        Notification savedNotification = notificationRepository.save(notification);

        //DTO로 변환
        NotificationResDTO notificationResDTO = NotificationMapper.INSTANCE
                .toNotificationResDTOFromCommentNotificationEntity(notification,comment.getSenderNickname());
        log.info("댓글 알림 DTO로 변환 : {}",notificationResDTO);

        //해당 유저에게 알림 전송
        template.convertAndSendToUser(comment.getReceiverId(), "/queue/notifications", notificationResDTO);

        log.info("댓글 알림 전송 끝 - \n 받을 유저 : {}, \n 받는 정보 : {}, \n 구독 경로 : {}",
                comment.getReceiverId(),
                notificationResDTO,
                "/queue/notifications");
    }

}
