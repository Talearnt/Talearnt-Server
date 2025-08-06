package com.talearnt.stomp.notification;


import com.talearnt.stomp.notification.response.NotificationResDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
public class NotificationController implements NotificationApi {
    private final SimpMessagingTemplate template;

    @MessageMapping("/notifications") // 클라이언트가 "/pub/notifications"로 메시지를 보낼 때 이 메소드가 호출됩니다.
    public NotificationResDTO messageHandler(NotificationResDTO notification) {
        // 알림을 전송하는 로직
        // 예시로 "/sub/notification" 채널로 알림을 전송
        log.info("받은 메세지 : {}", notification);
        template.convertAndSend("/sub/notification", notification);
        return notification; // 클라이언트에게 응답으로 알림 객체를 반환
    }

    @GetMapping("/pub/notifications")
    public NotificationResDTO dummyNotification(NotificationResDTO notification) {
        return null;
    }

}
