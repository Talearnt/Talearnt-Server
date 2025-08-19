package com.talearnt.stomp.notification.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NotificationSettingReqDTO {
    
    // 맞춤 키워드 알림 설정
    private boolean allowKeywordNotifications;
    
    // 댓글/답글 알림 설정 (하나로 통합)
    private boolean allowCommentNotifications;
}
