package com.talearnt.comment.community;

import com.talearnt.comment.community.request.CommentReqDTO;
import com.talearnt.comment.community.response.CommentListResDTO;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.response.PaginatedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface CommentApi {

    @Operation(summary = "커뮤니티 댓글 작성",
            description = "<h2>내용</h2>" +
                    "<p>커뮤니티 댓글 작성입니다.</p>" +
                    "<h2>Request</h2>" +
                    "<ul>" +
                    "<li>communityPostNo : 게시글 번호</li>" +
                    "<li>content : 댓글 내용 - 3자 이상, 300자 이하</li>" +
                    "</ul>" +
                    "<h2>Response</h2>" +
                    "<ul>" +
                    "<li>commentNo : 등록된 댓글 번호</li>" +
                    "</ul>"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", ref = "EXPIRED_TOKEN"),
            @ApiResponse(responseCode = "400-1", ref = "COMMENT_MISMATCH_POST_NUMBER"),
            @ApiResponse(responseCode = "400-2", ref = "COMMENT_CONTENT_OVER_LENGTH"),
            @ApiResponse(responseCode = "404", ref = "COMMENT_NOT_FOUND_POST")
    })
    public ResponseEntity<CommonResponse<Long>> addComment(@RequestBody @Valid CommentReqDTO commentReqDTO);


    @Operation(summary = "커뮤니티 게시글 댓글 목록",
            description = "<h2>내용</h2>" +
                    "<p>댓글 목록입니다.</p>" +
                    "<p>커뮤니티 게시글 번호에 해당하는 댓글을 가져옵니다.</p>" +
                    "<hr>" +
                    "<h2>웹 참고 내용</h2>" +
                    "<p>lastNo 포함 시 제대로 된 결과 값이 반환되지 않아 예외적으로 Exception을 발생 시킵니다.</p>" +
                    "<hr>" +
                    "<h2>모바일 참고 내용</h2>" +
                    "<p>Page가 2이상일 경우 제대로 된 결과 값이 반환되지 않아 예외적으로 Exception을 발생 시킵니다.</p>" +
                    "<hr>" +
                    "<h2>Response</h2>"+
                    "<ul>" +
                        "<li>userNo : 회원 번호 (내 댓글 파악 또는 작성자 댓글 파악용)</li>" +
                        "<li>nickname : 회원 닉네임</li>" +
                        "<li>profileImg : 회원 프로필 이미지</li>" +
                        "<li>commentNo : 댓글 번호 (답글 조회 및 답글 작성 시 필요)</li>" +
                        "<li>content : 게시글 내용 - 3자 이상, 300자 이하</li>" +
                        "<li>createdAt : 댓글 작성일</li>" +
                        "<li>updatedAt : 댓글 수정일 (수정 안했을 시 null 반환)</li>" +
                        "<li>replyCount : 댓글의 답글 갯수</li>" +
                    "</ul>" +
                    "<hr>"+
                    "<h2>pagination - Mobile</h2>" +
                    "<ul>" +
                        "<li>hasNext : 다음 게시글 이동 가능 여부</li>" +
                    "</ul>" +
                    "<hr>" +
                    "<h2>pagination - Web</h2>" +
                    "<ul>" +
                        "<li>hasNext - 다음 페이지 이동 가능 여부</li>" +
                        "<li>hasPrevious - 이전 페이지 이동 가능 여부</li>" +
                        "<li>totalCount - 총 데이터 개수</li>" +
                        "<li>totalPages - 총 페이지 개수</li>" +
                        "<li>currentPage - 현재 페이지 번호</li>" +
                        "<li>latestCreatedAt - 가장 최근 Data 작성일</li>" +
                    "</ul>")
    public ResponseEntity<CommonResponse<PaginatedResponse<List<CommentListResDTO>>>> getCommentList(
            @PathVariable @Schema(description = "커뮤니티 게시글 번호") Long postNo,
            @RequestParam(required = false, defaultValue = "web") @Schema(defaultValue = "web", description = "web|mobile") String path,
            @RequestParam(required = false) @Schema(description = "마지막 게시글 번호") String lastNo,
            @RequestParam(required = false, defaultValue = "1") @Schema(defaultValue = "1", description = "Mobile은 무조건 1") String page,
            @RequestParam(required = false, defaultValue = "10") @Schema(defaultValue = "10", description = "댓글 개수") String size);

}
