package com.talearnt.comment.community.response;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CommentListResDTO {
    private Long userNo;
    private String nickname;
    private String profileImg;

    private Long commentNo;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long replyCount;
}
