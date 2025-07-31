package com.talearnt.reply.community.response;

import com.talearnt.enums.post.PostType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyRepliesResDTO {
    private Long postNo;
    private PostType postType;
    private String postTitle;
    private Long replyNo;
    private String replyContent;
    private LocalDateTime replyCreatedAt;
    private LocalDateTime replyUpdatedAt;
}
