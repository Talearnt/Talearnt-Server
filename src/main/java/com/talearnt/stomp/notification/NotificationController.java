package com.talearnt.stomp.notification;


import com.talearnt.stomp.notification.response.NotificationResDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** 알림은 문서를 남길 수 없어서 문서 남기는 대용으로 쓰는 Controller입니다.
 * 이곳에서 이뤄지는 작업은 현재 없습니다.
 * */



@RestController
@RequiredArgsConstructor
@Log4j2
public class NotificationController implements NotificationApi {

    @GetMapping("/sub/notifications")
    public NotificationResDTO dummyNotification(NotificationResDTO notification) {
        return null;
    }

}
