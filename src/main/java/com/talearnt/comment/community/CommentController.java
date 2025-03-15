package com.talearnt.comment.community;

import com.talearnt.comment.community.request.CommentReqDTO;
import com.talearnt.comment.community.response.CommentListResDTO;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.response.PaginatedResponse;
import com.talearnt.util.version.RestControllerV1;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestControllerV1
@RequiredArgsConstructor
@Tag(name = "Comment")
public class CommentController implements CommentApi {

    private final CommentService commentService;

    @PostMapping("/communities/comments")
    public ResponseEntity<CommonResponse<Long>> addComment(@RequestBody @Valid CommentReqDTO commentReqDTO) {
        return CommonResponse.success(commentService.addComment(commentReqDTO));
    }

    @GetMapping("/communties/{postNo}/comments")
    public ResponseEntity<CommonResponse<PaginatedResponse<List<CommentListResDTO>>>> getCommentList(
            @PathVariable Long postNo,
            @RequestParam(required = false, defaultValue = "web") String path,
            @RequestParam(required = false) String lastNo,
            @RequestParam(required = false, defaultValue = "1") String page,
            @RequestParam(required = false, defaultValue = "10") String size) {
        return CommonResponse.success(commentService.getCommunityComments(postNo, path, lastNo, page, size));
    }
}
