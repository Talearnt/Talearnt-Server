package com.talearnt.stomp.notification;


import com.talearnt.stomp.notification.request.NotificationReqDTO;
import com.talearnt.stomp.notification.response.NotificationResDTO;
import com.talearnt.stomp.notification.response.NotificationSettingResDTO;
import com.talearnt.util.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 알림은 문서를 남길 수 없어서 문서 남기는 대용으로 쓰는 Controller입니다.
 * 이곳에서 이뤄지는 작업은 현재 없습니다.
 * */



@RestController
@RequiredArgsConstructor
@Log4j2
public class NotificationController implements NotificationApi {

    private final NotificationService notificationService;


    @GetMapping("/queue/notifications")
    public NotificationResDTO dummyNotification(NotificationResDTO notification) {
        return null;
    }


    @GetMapping("/notifications")
    public ResponseEntity<CommonResponse<List<NotificationResDTO>>> getNotifications(Authentication authentication) {
        List<NotificationResDTO> notifications = notificationService.getNotifications(authentication);
        return CommonResponse.success(notifications);
    }

    @PutMapping("/notifications/read")
    public ResponseEntity<CommonResponse<Void>> readNotification(@RequestBody NotificationReqDTO notificationReqDTO, Authentication authentication) {
        notificationService.readNotification(notificationReqDTO.getNotificationNos(), authentication);
        return CommonResponse.success(null);
    }

    @DeleteMapping("/notifications")
    public ResponseEntity<CommonResponse<Void>> deleteNotification(@RequestBody NotificationReqDTO notificationReqDTO, Authentication authentication) {
        notificationService.deleteNotification(notificationReqDTO.getNotificationNos(),authentication);
        return CommonResponse.success(null);
    }

    @GetMapping("/notifications/settings")
    public ResponseEntity<CommonResponse<NotificationSettingResDTO>> getNotificationSettings(Authentication authentication) {
        return CommonResponse.success(notificationService.getNotificationSettings(authentication));
    }

}
