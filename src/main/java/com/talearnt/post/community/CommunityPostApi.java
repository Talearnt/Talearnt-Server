package com.talearnt.post.community;

import com.talearnt.post.community.request.CommunityPostReqDTO;
import com.talearnt.post.community.response.CommunityPostDetailResDTO;
import com.talearnt.util.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface CommunityPostApi {


    @Operation(summary = "커뮤니티 게시글 작성"
            , description = "<h2>내용</h2>" +
            "<p>커뮤니티 게시글 작성입니다.</p>" +
            "<p>postType 으로 자유,질문,스터디 게시판이 나뉘어집니다.</p>" +
            "<p>이미지 모아보기가 없어도 이미지 삭제를 위하여 imagesUrls를 여기서도 적용하고 있습니다.</p>" +
            "<hr/>" +
            "<h2>Request</h2>" +
            "<ul>" +
                "<li>title : 제목 - 2자 이상 50자 이하</li>" +
                "<li>content : 내용 - 20자 이상 1000자 이하 (불상사를 막기위하여 DB는 TEXT 타입)</li>" +
                "<li>postType : 커뮤니티 게시글 타입 - (EXCHANGE|FREE|QUESTION|STUDY)</li>" +
                "<li>imageUrls : 이미지 경로들</li>" +
            "</ul>" +
            "<p>postType에 Exchange가 있는 이유는 이미지 업로드 시 필요한 데이터 값이어서 그렇습니다. 클라이언트에서 사용할 일은 없습니다.</p>" +
            "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400",ref="FILE_FAILED_UPLOAD"),
            @ApiResponse(responseCode = "401", ref = "EXPIRED_TOKEN"),
    })
    public ResponseEntity<CommonResponse<String>> addCommunityPost(@Valid @RequestBody CommunityPostReqDTO communityPostReqDTO);

    @Operation(summary = "커뮤니티 게시글 상세보기"
            , description = "<h2>내용</h2>" +
            "<p>커뮤니티 게시글 상세보기입니다.</p>" +
            "<p>댓글은 따로 API 요청을 보내주셔야 합니다.</p>" +
            "<p>댓글 갯수는 댓글 갯수 + 답글 갯수 입니다.</p>" +
            "<hr/>" +
            "<h2>Response</h2>" +
            "<ul>" +
            "<li>userNo : 유저 번호 (본인 게시물 확인용)</li>" +
            "<li>nickname : 유저 닉네임</li>" +
            "<li>profileImg : 프로필 사진 경로</li>" +
            "<li>authority : 유저 권한</li>" +
            "<li>title : 제목</li>" +
            "<li>content : 내용</li>" +
            "<li>postType : 게시글 타입 (FREE|QUESTION|STUDY)</li>" +
            "<li>count : 조회수</li>" +
            "<li>isLike : 게시글 좋아요 여부</li>" +
            "<li>commentCount : 총 댓글 개수(댓글 수+답글 수)</li>" +
            "<li>createdAt : 게시글 생성 일시</li>" +
            "</ul>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404",ref="POST_NOT_FOUND"),
    })
    public ResponseEntity<CommonResponse<CommunityPostDetailResDTO>> getCommunityPostDetail(@PathVariable Long postNo, Authentication authentication);


    @Operation(summary = "커뮤니티 게시글 수정"
            , description = "<h2>내용</h2>" +
            "<p>커뮤니티 게시글 수정입니다.</p>" +
            "<p>postType은 FREE|QUESTION|STUDY 중 하나를 택하여 보내주시면 되겠습니다.</p>" +
            "<p>이미지 수정은 S3에 직접 업로드된 경우가 필요하여 FE에서 완성할 경우 테스트 해보도록 하겠습니다.</p>" +
            "<p><strong>삭제된 게시글은 수정할 수 없습니다.</strong></p>" +
            "<h2>Request</h2>" +
            "<ul>" +
                "<li>title : 제목 - 2자 이상 50자 이하</li>" +
                "<li>content : 내용 - 20자 이상 1000자 이하 (불상사를 막기위하여 DB는 TEXT 타입)</li>" +
                "<li>postType : 커뮤니티 게시글 타입 - (EXCHANGE|FREE|QUESTION|STUDY)</li>" +
                "<li>imageUrls : 이미지 경로들</li>" +
            "</ul>"
           )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401",ref="EXPIRED_TOKEN"),
            @ApiResponse(responseCode = "403",ref="POST_ACCESS_DENIED"),
            @ApiResponse(responseCode = "400",ref="POST_FAILED_UPDATE"),
    })
    public ResponseEntity<CommonResponse<Void>> updateCommunityPost(@PathVariable Long postNo, @RequestBody CommunityPostReqDTO communityPostReqDTO);

    @Operation(summary = "커뮤니티 게시글 삭제"
            , description = "<h2>내용</h2>" +
            "<p>커뮤니티 게시글 삭제입니다.</p>" +
            "<p>이미 삭제된 게시글은 삭제되지 않습니다.</p>" +
            "<p>커뮤니티 게시글 삭제 시 S3에 존재하는 이미지 삭제 부분은 구현하지 않았습니다. 기획이 완성되는 대로 이미지 삭제 여부를 확인하겠습니다.</p>"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401",ref="EXPIRED_TOKEN"),
            @ApiResponse(responseCode = "403",ref="POST_ACCESS_DENIED"),
            @ApiResponse(responseCode = "400",ref="POST_FAILED_DELETE"),
    })
    public ResponseEntity<CommonResponse<Void>> deleteCommunityPost(@PathVariable Long postNo, Authentication authentication);

}
