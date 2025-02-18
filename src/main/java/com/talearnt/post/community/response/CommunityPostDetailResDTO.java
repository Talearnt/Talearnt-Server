package com.talearnt.post.community.response;

import com.talearnt.enums.post.PostType;
import com.talearnt.enums.user.UserRole;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CommunityPostDetailResDTO {
    private Long userNo;
    private String nickname;
    private String profileImg;
    private UserRole authority;

    private String title;
    private String content;
    private PostType postType;
    private Long count;
    private boolean isLike;
    private Integer commentCount;
    private LocalDateTime createdAt;
}
