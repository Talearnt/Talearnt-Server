package com.talearnt.stomp.notification.response;


import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NotificationSettingResDTO {
    private boolean allowCommentNotifications; // 댓글 알림 허용 여부
    private boolean allowKeywordNotifications; // 관심 키워드 알림 허용 여부
}
