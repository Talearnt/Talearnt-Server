package com.talearnt.post.community;

import com.talearnt.post.community.request.CommunityPostReqDTO;
import com.talearnt.post.community.response.CommunityPostDetailResDTO;
import com.talearnt.post.community.response.CommunityPostListResDTO;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.response.PaginatedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CommunityPostApi {

    @Operation(summary = "커뮤니티 게시글 목록"
            , description = "<h2>내용</h2>" +
            "<p>커뮤니케이션 목록입니다.</p>" +
            "<p>웹과 모바일의 Response에 차이가 있습니다. Mobile에는 content가 추가됩니다.</p>" +
            "<p>path에 (mobile|web) 으로 보내주셔야하고 보내지 않을 경우에는 web으로 기본 설정 되어 있습니다.</p>" +
            "<p>커뮤니티 게시글의 order는 (recent|hot)으로 구성 되어 있고, 기본 값은 recent입니다.</p>" +
            "<hr/>" +
            "<h2>웹 참고 내용</h2>" +
            "<p>웹은 Page하고 Size만 보내주시면 됩니다.</p>" +
            "<p>Last No를 포함할 시 예외적으로 목록 조회 Exception이 발생합니다.</p>" +
            "<p>order가 hot이면 모바일처럼 Content가 포함되어 넘어갑니다.</p>" +
            "<hr/>" +
            "<h2>모바일 참고 내용</h2>" +
            "<p>첫 게시글 호출 시 path, order, size만 보내고 2페이지 이상 호출 시 lastNo를 추가하시면 됩니다.</p>" +
            "<p>order : recent(최신순) 과 LastNo : 게시글 마지막 번호를 같이 보내면 최신순 다음 페이지 호출입니다.</p>" +
            "<p>order : hot(BEST 커뮤니티) 와 LastNo : 게시글 마지막 번호를 같이 보내면 BEST 커뮤니티 게시글 다음 페이지 호출입니다.</p>" +
            "<p>page는 무조건 1로 보내셔야 합니다.</p>" +
            "<p>size는 자유입니다. 기본 12로 구현되어 있습니다.</p>" +
            "<hr/>" +
            "<h2>Response - 공통</h2>" +
            "<ul>" +
                "<li>profileImg : 작성자의 프로필 이미지 경로</li>" +
                "<li>nickname : 작성자의 닉네임</li>" +
                "<li>authority : 작성자의 권한 (인증 유저)</li>" +
                "<li>communityPostNo : 게시글 번호</li>" +
                "<li>postType : 커뮤니티 게시글 타입 - (자유 게시판|질문 게시판|스터디 게시판)</li>" +
                "<li>title : 게시글 제목</li>" +
                "<li>count : 게시글 조회수</li>" +
                "<li>commentCount : (댓글 수+답글 수) 개수</li>" +
                "<li>likeCount : 게시글 좋아요 개수</li>" +
                "<li>isLike : 게시글 좋아요 여부(true:좋아요, false:안누름)</li>" +
                "<li>createdAt : 게시글 작성일</li>" +
            "</ul>" +
            "<hr/>" +
            "<h2>Response - 모바일에서 추가</h2>" +
            "<ul>" +
                "<li>content : 게시글 내용</li>" +
            "</ul>" +
            "<hr/>" +
            "<h2>pagination - Mobile</h2>" +
            "<ul>" +
                "<li>hasNext : 다음 게시글 이동 가능 여부</li>" +
            "</ul>" +
            "<h2>pagination - Web</h2>" +
            "<ul>" +
                "<li>hasNext - 다음 페이지 이동 가능 여부</li>" +
                "<li>hasPrevious - 이전 페이지 이동 가능 여부</li>" +
                "<li>totalCount - 총 데이터 개수</li>" +
                "<li>totalPages - 총 페이지 개수</li>" +
                "<li>currentPage - 현재 페이지 번호</li>" +
                "<li>latestCreatedAt - 가장 최근 Data 작성일</li>" +
            "</ul>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200")
    })
    public ResponseEntity<CommonResponse<PaginatedResponse<List<CommunityPostListResDTO>>>> getCommunityPostList(
            @RequestParam(required = false) String postType,
            @RequestParam(required = false, defaultValue = "recent") String order,
            @RequestParam(required = false, defaultValue = "web") String path,
            @RequestParam(required = false, defaultValue = "1") String page,
            @RequestParam(required = false, defaultValue = "12") String size,
            @RequestParam(required = false) String lastNo,
            Authentication authentication);

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
                "<li>postType : 커뮤니티 게시글 타입 - (자유 게시판|질문 게시판|스터디 모집 게시판)</li>" +
                "<li>imageUrls : 이미지 경로들</li>" +
            "</ul>" +
            "<p>postType에 Exchange가 있는 이유는 이미지 업로드 시 필요한 데이터 값이어서 그렇습니다. 클라이언트에서 사용할 일은 없습니다.</p>" +
            "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400",ref="FILE_FAILED_UPLOAD"),
            @ApiResponse(responseCode = "401", ref = "EXPIRED_TOKEN"),
    })
    public ResponseEntity<CommonResponse<Long>> addCommunityPost(@Valid @RequestBody CommunityPostReqDTO communityPostReqDTO);

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
            "<li>communityPostNo : 게시글 번호</li>" +
            "<li>title : 제목</li>" +
            "<li>content : 내용</li>" +
            "<li>postType : 게시글 타입 </li>" +
            "<li>imageUrls : 이미지 경로 목록</li>" +
            "<li>count : 조회수</li>" +
            "<li>isLike : 게시글 좋아요 여부</li>" +
            "<li>likeCount : 게시글 좋아요 개수</li>" +
            "<li>commentCount : 총 댓글 개수(댓글 수+답글 수)</li>" +
            "<li>commentLastPage : 게시글 댓글의 마지막 페이지 번호</li>" +
            "<li>createdAt : 게시글 생성 일시</li>" +
            "<li>updatedAt : 게시글 수정 일시</li>" +
            "</ul>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404",ref="POST_NOT_FOUND"),
    })
    public ResponseEntity<CommonResponse<CommunityPostDetailResDTO>> getCommunityPostDetail(@PathVariable Long postNo, Authentication authentication);


    @Operation(summary = "커뮤니티 게시글 수정"
            , description = "<h2>내용</h2>" +
            "<p>커뮤니티 게시글 수정입니다.</p>" +
            "<p>postType은 자유 게시판|질문 게시판|스터디 모집 게시판 중 하나를 택하여 보내주시면 되겠습니다.</p>" +
            "<p>이미지 수정은 S3에 직접 업로드된 경우가 필요하여 FE에서 완성할 경우 테스트 해보도록 하겠습니다.</p>" +
            "<p><strong>삭제된 게시글은 수정할 수 없습니다.</strong></p>" +
            "<h2>Request</h2>" +
            "<ul>" +
                "<li>title : 제목 - 2자 이상 50자 이하</li>" +
                "<li>content : 내용 - 20자 이상 1000자 이하 (불상사를 막기위하여 DB는 TEXT 타입)</li>" +
                "<li>postType : 커뮤니티 게시글 타입 - (자유 게시판|질문 게시판|스터디 모집 게시판)</li>" +
                "<li>imageUrls : 이미지 경로들</li>" +
            "</ul>"
           )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401",ref="EXPIRED_TOKEN"),
            @ApiResponse(responseCode = "403",ref="POST_ACCESS_DENIED"),
            @ApiResponse(responseCode = "404",ref="POST_NOT_FOUND"),
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
            @ApiResponse(responseCode = "404",ref="POST_NOT_FOUND"),
    })
    public ResponseEntity<CommonResponse<Void>> deleteCommunityPost(@PathVariable Long postNo, Authentication authentication);



    @Operation(summary = "커뮤니티 게시글 좋아요"
            , description = "<h2>내용</h2>" +
            "<p>커뮤니티 게시글 좋아요입니다. 토글 방식으로 작동합니다.</p>" +
            "<p>비동기 방식으로 작동하기 때문에 좋아요 버튼을 눌렀을 경우 1~2초간 서버에 요청 보내는 것을 제어해야 합니다.</p>" +
            "<p>제어하지 않을 경우 서버에 부화가 생겨 문제가 발생할 수 있습니다.</p>" +
            "<p>커뮤니티 게시글 좋아요는 반환값이 없습니다.</p>" +
            "<p>좋아요 갯수를 반환할까도 싶었지만, 실시간으로 좋아요 변동이 필요하지 않을 것이라 판단해 프론트 쪽에서 좋아요 갯수를 제어해주시면 될 것 같습니다.</p>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401",ref="EXPIRED_TOKEN"),
            @ApiResponse(responseCode = "404",ref="POST_NOT_FOUND")
    })
    public ResponseEntity<CommonResponse<CompletableFuture<Void>>> likeCommunityPost(@PathVariable Long postNo, Authentication authentication);
}
