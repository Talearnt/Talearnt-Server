package com.talearnt.comment.community;

import com.talearnt.comment.community.request.CommentReqDTO;
import com.talearnt.comment.community.request.CommentUpdateReqDTO;
import com.talearnt.comment.community.response.CommentListResDTO;
import com.talearnt.enums.common.ErrorCode;
import com.talearnt.enums.common.Regex;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.response.PaginatedResponse;
import com.talearnt.util.valid.DynamicValid;
import com.talearnt.util.version.RestControllerV1;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestControllerV1
@RequiredArgsConstructor
@Tag(name = "Comment")
@Validated
public class CommentController implements CommentApi {

    private final CommentService commentService;

    @PostMapping("/comments/communities")
    public ResponseEntity<CommonResponse<Long>> addComment(@RequestBody @Valid CommentReqDTO commentReqDTO) {
        return CommonResponse.success(commentService.addComment(commentReqDTO.getUserInfo().getUserNo(),
                commentReqDTO.getCommunityPostNo(),
                commentReqDTO.getContent()));
    }

    @GetMapping("/comments/communities/{postNo}")
    public ResponseEntity<CommonResponse<PaginatedResponse<List<CommentListResDTO>>>> getCommentList(
            @PathVariable Long postNo,
            @RequestParam(required = false, defaultValue = "web") String path,
            @RequestParam(required = false) String lastNo,
            @RequestParam(required = false, defaultValue = "1") String page,
            @RequestParam(required = false, defaultValue = "10") String size) {
        return CommonResponse.success(commentService.getCommunityComments(postNo, path, lastNo, page, size));
    }


    @PutMapping("/comments/{commentNo}/communities")
    public ResponseEntity<CommonResponse<Void>> updateComment(@PathVariable @DynamicValid(errorCode = ErrorCode.COMMENT_MISMATCH_NUMBER, pattern = Regex.NUMBER_TYPE_PRIMARY_KEY) Long commentNo,
                                                              @RequestBody @Valid CommentUpdateReqDTO commentUpdateReqDTO) {
        return CommonResponse.success(commentService.updateComment(
                commentUpdateReqDTO.getUserInfo().getUserNo()
                , commentNo
                , commentUpdateReqDTO.getContent()));
    }

    @DeleteMapping("/comments/{commentNo}/communities")
    public ResponseEntity<CommonResponse<Void>> deleteComment(@PathVariable @DynamicValid(errorCode = ErrorCode.COMMENT_MISMATCH_NUMBER, pattern = Regex.NUMBER_TYPE_PRIMARY_KEY) Long commentNo,
                                                              Authentication authentication) {
        return CommonResponse.success(commentService.deleteComment(authentication, commentNo));
    }

}
