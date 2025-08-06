package com.talearnt.stomp.notification;

import com.talearnt.stomp.notification.response.NotificationResDTO;
import io.swagger.v3.oas.annotations.Operation;

public interface NotificationApi {



    @Operation(summary = "알림 테스트",
            tags = "Notification",
    description = "알림 테스트를 위한 API로, 클라이언트가 '/pub/notifications'로 메시지를 보내면 이 메소드가 호출되어 알림을 처리합니다.")
    public NotificationResDTO dummyNotification(NotificationResDTO notification);
}
