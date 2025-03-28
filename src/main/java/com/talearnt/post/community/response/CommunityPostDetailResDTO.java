package com.talearnt.post.community.response;

import com.talearnt.enums.post.PostType;
import com.talearnt.enums.user.UserRole;
import com.talearnt.util.common.PostUtil;
import com.talearnt.util.common.SplitUtil;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@NoArgsConstructor
@ToString
public class CommunityPostDetailResDTO {
    private Long userNo;
    private String nickname;
    private String profileImg;
    private UserRole authority;

    private Long communityPostNo;
    private String title;
    private String content;
    private PostType postType;
    private List<String> imageUrls;
    private Integer count;
    private Boolean isLike;
    private long likeCount;
    private long commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public CommunityPostDetailResDTO(Long userNo, String nickname, String profileImg, UserRole authority, Long communityPostNo, String title, String content, PostType postType, String imageUrls, Integer count, Boolean isLike, long likeCount, long commentCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userNo = userNo;
        this.nickname = nickname;
        this.profileImg = profileImg;
        this.authority = authority;
        this.communityPostNo = communityPostNo;
        this.title = title;
        this.content = content;
        this.postType = postType;
        this.imageUrls = SplitUtil.splitStringToList(imageUrls);
        this.count = count;
        this.isLike = isLike;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
