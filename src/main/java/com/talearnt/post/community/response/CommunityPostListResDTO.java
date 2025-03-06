package com.talearnt.post.community.response;

import com.talearnt.enums.post.PostType;
import com.talearnt.enums.user.UserRole;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;


@SuperBuilder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CommunityPostListResDTO {
    private String profileImg;
    private String nickname;
    private UserRole authority;

    private Long communityPostNo;
    private PostType postType;
    private String title;
    private int count;
    private long commentCount;
    private long likeCount;
    private Boolean isLike;
    private LocalDateTime createdAt;
}
