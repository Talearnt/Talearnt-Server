package com.talearnt.reply.community;

import com.talearnt.reply.community.response.ReplyListResDTO;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.response.PaginatedResponse;
import com.talearnt.util.version.RestControllerV1;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestControllerV1
@Log4j2
@RequiredArgsConstructor
@Tag(name = "Comment-Community & Reply")
public class ReplyController implements ReplyApi {

    private final ReplyService replyService;

    
    //커뮤니티 답글 목록 조회
    @GetMapping("/replies/communities/{commentNo}")
    public ResponseEntity<CommonResponse<PaginatedResponse<List<ReplyListResDTO>>>> getReplies(@PathVariable Long commentNo,
                                                                                               @RequestParam(required = false) String lastNo,
                                                                                               @RequestParam(required = false, defaultValue = "10") String size) {


        return CommonResponse.success(replyService.getReplies(commentNo, lastNo, size));
    }


}
