package com.talearnt.stomp.notification.request;

import lombok.*;

import java.util.List;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NotificationReqDTO {
    private List<Long> notificationNos;
}
