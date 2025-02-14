package com.talearnt.post.community;

import com.talearnt.post.community.request.CommunityPostReqDTO;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.version.RestControllerV1;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Log4j2
@RequiredArgsConstructor
@RestControllerV1
@Tag(name = "Post-Community")
public class CommunityPostController implements CommunityPostApi{

    private final CommunityPostService communityPostService;

    @PostMapping("/posts/communities")
    public ResponseEntity<CommonResponse<String>> addCommunityPost(@Valid @RequestBody CommunityPostReqDTO communityPostReqDTO){
        return CommonResponse.success(communityPostService.addCommunityPost(communityPostReqDTO));
    }

}
