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
public class CommunityPostMobileListResDTO extends CommunityPostListResDTO{
    private String content;

    public CommunityPostMobileListResDTO(String profileImg, String nickname, UserRole authority, Long communityPostNo, PostType postType, String title, int count, long commentCount, long likeCount, Boolean isLike, LocalDateTime createdAt, String content) {
        super(profileImg,nickname,authority,communityPostNo,postType,title,count,commentCount,likeCount,isLike,createdAt);
        this.content = content;
    }
}
