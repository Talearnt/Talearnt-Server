package com.talearnt.post.community;

import com.talearnt.enums.common.ClientPathType;
import com.talearnt.post.community.request.CommunityPostReqDTO;
import com.talearnt.post.community.response.CommunityPostDetailResDTO;
import com.talearnt.post.community.response.CommunityPostListResDTO;
import com.talearnt.util.common.ClientPath;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.response.PaginatedResponse;
import com.talearnt.util.version.RestControllerV1;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RequiredArgsConstructor
@RestControllerV1
@Tag(name = "Post-Community")
public class CommunityPostController implements CommunityPostApi{

    private final CommunityPostService communityPostService;

    //커뮤니티 게시글 목록
    @GetMapping("/posts/communities")
    public ResponseEntity<CommonResponse<PaginatedResponse<List<CommunityPostListResDTO>>>> getCommunityPostList(
            @RequestParam(required = false) String postType,
            @RequestParam(required = false, defaultValue = "recent") String order,
            @ClientPath ClientPathType path,
            @RequestParam(required = false, defaultValue = "1") String page,
            @RequestParam(required = false, defaultValue = "12") String size,
            @RequestParam(required = false) String lastNo,
            Authentication authentication){

        return CommonResponse.success(communityPostService.getCommunityPostList(authentication,postType,order,path.name(),lastNo,page,size));
    }

    //커뮤니티 게시글 상세보기
    @GetMapping("/posts/communities/{postNo}")
    public ResponseEntity<CommonResponse<CommunityPostDetailResDTO>> getCommunityPostDetail(@PathVariable Long postNo, Authentication authentication){
        return CommonResponse.success(communityPostService.getCommunityPostDetail(postNo, authentication));
    }

    //커뮤니티 게시글 작성
    @PostMapping("/posts/communities")
    public ResponseEntity<CommonResponse<Long>> addCommunityPost(@Valid @RequestBody CommunityPostReqDTO communityPostReqDTO){
        return CommonResponse.success(communityPostService.addCommunityPost(communityPostReqDTO));
    }

    //커뮤니티 게시글 수정
    @PutMapping("/posts/communities/{postNo}")
    public ResponseEntity<CommonResponse<Void>> updateCommunityPost(@PathVariable Long postNo, @RequestBody CommunityPostReqDTO communityPostReqDTO){
        return CommonResponse.success(communityPostService.updateCommunityPost(postNo,communityPostReqDTO));
    }

    //커뮤니티 게시글 삭제
    @DeleteMapping("/posts/communities/{postNo}")
    public ResponseEntity<CommonResponse<Void>> deleteCommunityPost(@PathVariable Long postNo, Authentication authentication){
        return CommonResponse.success(communityPostService.deleteCommunityPost(postNo, authentication));
    }

    //커뮤니티 게시글 좋아요
    @PostMapping("/posts/communities/{postNo}/like")
    public ResponseEntity<CommonResponse<Void>> likeCommunityPost(@PathVariable Long postNo, Authentication authentication){
        communityPostService.likeCommunityPost(postNo, authentication);
        return CommonResponse.success(null);
    }


}
