package com.talearnt.post.community.request;

import com.talearnt.util.common.RequiredJwtValueDTO;
import com.talearnt.util.jwt.UserInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@RequiredJwtValueDTO
public class CommunityPostLikeStatusReqDTO {
    @Schema(hidden = true)
    private UserInfo userInfo;

    private boolean isLike;
}
