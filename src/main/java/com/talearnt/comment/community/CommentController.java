package com.talearnt.comment.community;

import com.talearnt.comment.community.request.CommentReqDTO;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.version.RestControllerV1;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@RestControllerV1
@RequiredArgsConstructor
@Tag(name = "Comment")
public class CommentController {

    private final CommentService commentService;

    public ResponseEntity<CommonResponse<Long>> addComment(@RequestBody @Valid CommentReqDTO commentReqDTO) {

        return CommonResponse.success(commentService.addComment(commentReqDTO));
    }

}
