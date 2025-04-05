package com.talearnt.reply.community.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReplyListResDTO {
    private Long userNo;
    private String nickname;
    private String profileImg;

    private Long replyNo;
    private Long commentNo;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
