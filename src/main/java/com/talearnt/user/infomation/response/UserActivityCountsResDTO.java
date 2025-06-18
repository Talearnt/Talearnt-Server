package com.talearnt.user.infomation.response;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserActivityCountsResDTO {
    private Long favoritePostCount; // 내가 찜한 게시글 수
    private Long myPostCount; // 내가 작성한 게시글 수
    private Long myCommentCount; // 내가 작성한 댓글 수
}
