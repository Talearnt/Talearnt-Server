package com.talearnt.comment.community.response;

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
public class MyCommentsResDTO {
    private Long postNo;
    private PostType postType;
    private String postTitle;
    private Long commentNo; //#을 붙여서 그 위치로 바로 가게 만들려면 필요
    private String commentContent;
    private LocalDateTime commentCreatedAt;
    private LocalDateTime commentUpdatedAt;
}
