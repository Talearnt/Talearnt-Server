package com.talearnt.post.community;

import com.talearnt.post.community.request.CommunityPostReqDTO;
import com.talearnt.post.community.response.CommunityPostDetailResDTO;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.version.RestControllerV1;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RequiredArgsConstructor
@RestControllerV1
@Tag(name = "Post-Community")
public class CommunityPostController implements CommunityPostApi{

    private final CommunityPostService communityPostService;

    //커뮤니티 게시글 상세보기
    @GetMapping("/posts/communities/{postNo}")
    public ResponseEntity<CommonResponse<CommunityPostDetailResDTO>> getCommunityPostDetail(@PathVariable Long postNo, Authentication authentication){
        return CommonResponse.success(communityPostService.getCommunityPostDetail(postNo, authentication));
    }

    //커뮤니티 게시글 작성
    @PostMapping("/posts/communities")
    public ResponseEntity<CommonResponse<String>> addCommunityPost(@Valid @RequestBody CommunityPostReqDTO communityPostReqDTO){
        return CommonResponse.success(communityPostService.addCommunityPost(communityPostReqDTO));
    }

    //커뮤니티 게시글 수정
    @PutMapping("/posts/communities/{postNo}")
    public ResponseEntity<CommonResponse<Void>> updateCommunityPost(@PathVariable Long postNo, @RequestBody CommunityPostReqDTO communityPostReqDTO){
        return CommonResponse.success(communityPostService.updateCommunityPost(postNo,communityPostReqDTO));
    }

}
