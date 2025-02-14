package com.talearnt.post.community.request;


import com.talearnt.enums.common.ErrorCode;
import com.talearnt.enums.common.Regex;
import com.talearnt.enums.post.PostType;
import com.talearnt.util.common.RequiredJwtValueDTO;
import com.talearnt.util.jwt.UserInfo;
import com.talearnt.util.valid.DynamicValid;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RequiredJwtValueDTO
public class CommunityPostReqDTO {

    @Schema(hidden = true)
    private UserInfo userInfo;

    @DynamicValid(errorCode = ErrorCode.POST_TITLE_LENGTH_MISSING, minLength = 2,maxLength = 50)
    private String title;

    @DynamicValid(errorCode = ErrorCode.POST_CONTENT_MIN_LENGTH, minLength = 20)
    private String content;


    @DynamicValid(errorCode = ErrorCode.POST_BAD_REQUEST, pattern = Regex.POST_TYPE)
    private PostType postType;

    private List<String> imageUrls; //S3에서 삭제할 파일 경로 PresignedURL 옵션 제거 경로
}
