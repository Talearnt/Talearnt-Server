package com.talearnt.reply.community;

import com.talearnt.reply.community.request.ReplyCreateReqDTO;
import com.talearnt.reply.community.request.ReplyUpdateReqDTO;
import com.talearnt.reply.community.response.ReplyListResDTO;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.response.PaginatedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ReplyApi {

    @Operation(summary = "커뮤니티 게시글 답글 목록", description = "<h2>내용</h2>" +
            "<p>답글 목록을 조회합니다.</p>" +
            "<p>모바일과 웹의 작동 방식이 같아서 Cursor 방식으로 구현했습니다.</p>" +
            "<p>LastNo가 있을 경우 목록의 첫 번째 값보다 작은 값을 가져옵니다 (가져오기->최신순 , 보여줄 땐 오래된 순)</p>" +
            "<h2>Response</h2>" +
            "<ul>" +
                "<li>userNo : 회원 번호</li>" +
                "<li>nickname : 회원 닉네임</li>" +
                "<li>profileImg : 회원 프로필 사진 경로</li>" +
                "<li>replyNo : 답글 번호</li>" +
                "<li>commentNo : 부모 댓글 번호</li>" +
                "<li>content : 답글 내용 </li>" +
                "<li>createdAt : 답글 작성일</li>" +
                "<li>updatedAt : 답글 수정일 (null이 아닐 경우 수정됨 표시)</li>" +
                "<br>" +
                "<li>hasNext : 다음 답글 더보기 가능 여부</li>" +
            "</ul>")
    ResponseEntity<CommonResponse<PaginatedResponse<List<ReplyListResDTO>>>> getReplies(@PathVariable Long commentNo,
                                                                                               @RequestParam(required = false) String lastNo,
                                                                                               @RequestParam(required = false, defaultValue = "10") String size);


    @Operation(summary = "커뮤니티 게시글 답글 작성",
            description = "<h2>내용</h2>" +
                    "<p>답글을 작성합니다.</p>" +
                    "<p>답글 작성 후 답글 번호를 포함한 값을 넘깁니다.</p>" +
                    "<h2>Request</h2>" +
                    "<ul>" +
                        "<li>commentNo : 부모 댓글 번호</li>" +
                        "<li>content : 답글 내용 </li>" +
                    "</ul>" +
                    "<h2>Response</h2>" +
                    "<ul>" +
                        "<li>userNo : 회원 번호</li>" +
                        "<li>nickname : 회원 닉네임</li>" +
                        "<li>profileImg : 회원 프로필 사진 경로</li>" +
                        "<li>replyNo : 답글 번호</li>" +
                        "<li>commentNo : 부모 댓글 번호</li>" +
                        "<li>content : 답글 내용 </li>" +
                        "<li>createdAt : 답글 작성일</li>" +
                        "<li>updatedAt : 답글 수정일 (null이 아닐 경우 수정됨 표시)</li>" +
                    "</ul>"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", ref = "EXPIRED_TOKEN"),
            @ApiResponse(responseCode = "404", ref = "COMMENT_MISMATCH_REPLY_NUMBER")
    })
    ResponseEntity<CommonResponse<ReplyListResDTO>> createReply(@RequestBody ReplyCreateReqDTO replyCreateReqDTO);


    @Operation(summary = "커뮤니티 게시글 답글 수정",
            description = "<h2>내용</h2>" +
                    "<p>답글을 수정합니다.</p>" +
                    "<h2>Request</h2>" +
                    "<ul>" +
                        "<li>content : 답글 내용 </li>" +
                    "</ul>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", ref = "EXPIRED_TOKEN"),
            @ApiResponse(responseCode = "404", ref = "REPLY_NOT_FOUND"),
            @ApiResponse(responseCode = "403", ref = "COMMENT_ACCESS_DINED")
    })
    ResponseEntity<CommonResponse<Void>> updateReply(@PathVariable Long replyNo,
                                                            @RequestBody ReplyUpdateReqDTO replyUpdateReqDTO);


    @Operation(summary = "커뮤니티 게시글 답글 삭제",
            description = "<h2>내용</h2>" +
                    "<p>답글을 삭제합니다.</p>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", ref = "EXPIRED_TOKEN"),
            @ApiResponse(responseCode = "404", ref = "REPLY_NOT_FOUND"),
            @ApiResponse(responseCode = "403", ref = "COMMENT_ACCESS_DINED")
    })
    ResponseEntity<CommonResponse<Void>> deleteReply(@PathVariable Long replyNo, Authentication authentication);

}
