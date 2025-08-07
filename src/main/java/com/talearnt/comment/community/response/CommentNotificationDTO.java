package com.talearnt.comment.community.response;

import com.talearnt.enums.stomp.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentNotificationDTO {
    private Long senderNo; // 알림을 보낸 사람의 번호
    private String senderNickname; // 알림을 보낸 사람의 닉네임
    private Long receiverNo; // 알림을 받을 사람의 번호
    private String receiverId; // 알림을 받을 사람의 아이디
    private Long targetNo; // 게시글, 댓글 등 알림의 대상이 되는 엔티티의 번호
    private String content; // 알림 내용
}
