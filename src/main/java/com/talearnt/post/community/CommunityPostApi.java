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

public interface CommunityPostApi {

    @Operation(summary = "커뮤니티 게시글 목록"
            , description = "<h2>내용</h2>" +
            "<p>커뮤니케이션 목록입니다.</p>" +
            "<p>웹과 모바일의 Response에 차이가 있습니다. Mobile에는 content가 추가됩니다.</p>" +
            "<p>postType은 (FREE|QUESTION|STUDY)가 있습니다. postType을 넣지 않은 경우는 '전체' 검색입니다.</p>" +
            "<p>path에 (mobile|web) 으로 보내주셔야하고 보내지 않을 경우에는 web으로 기본 설정 되어 있습니다.</p>" +
            "<p>커뮤니티 게시글의 order는 (recent|hot)으로 구성 되어 있고, 기본 값은 recent입니다.</p>" +
            "<p>baseTime은 첫 목록 호출 시간을 고정 값으로 보내셔야 합니다.</p>" +
            "<p>baseTime을 설정 안하면 서버에 요청한 시간으로 설정됩니다. (새로운 게시글 갱신될 가능성 있음)</p>" +
            "<p>ISO_DATE_TIME의 format이고 'yyyy-MM-ddTHH:mm:ss' 형식으로 보내주시면 됩니다.</p>" +
            "<hr/>" +
            "<h2>최신순 페이지네이션 필요 파라미터 및 조건</h2>"+
            "<p>최신순 게시물 첫 호출 시에는 order(recent)와 baseTime(첫 목록 호출 시간),path(접근 경로), page(기본값 : 1), size(기본 값: 12) 만 보내주시고</p>" +
            "<p>두 번째 페이지 요청하실 때 lastNo만 보내주시면 됩니다.</p>" +
            "<ul>" +
                "<li>order = recent</li>" +
                "<li>lastNo = 게시글 마지막 postNo</li>" +
                "<li>baseTime = 첫 목록 조회 시간 (이후 고정 값 - 새로운 게시글이 포함된 목록을 원할 시 갱신)</li>" +
                "<li>page = 현재 페이지 번호 + 1</li>" +
                "<li>size = 호출하고자 하는 게시글 개수</li>" +
            "</ul>" +
            "<hr/>" +
            "<h2>핫한 게시물 페이지네이션 필요 파라미터 및 조건</h2>"+
            "<p>핫한 게시물 목록 첫 호출 시에는 order(hot)과 baseTime(첫 목록 호출 시간),path(접근 경로), page(기본값 : 1), size(기본 값: 12) 만 보내주시고</p>" +
            "<p>두 번째 페이지 요청하실 때 lastNo와 popularScore(마지막 게시글 점수) 를 보내주시면 됩니다.</p>" +
            "<ul>" +
                "<li>order = hot</li>" +
                "<li>lastNo = 게시글 마지막 postNo</li>" +
                "<li>baseTime = 첫 목록 조회 시간 (이후 고정 값 - 새로운 게시글이 포함된 목록을 원할 시 갱신)</li>" +
                "<li>popularScore = 마지막 인기 점수</li>" +
                "<li>page = 현재 페이지 번호 + 1</li>" +
                "<li>size = 호출하고자 하는 게시글 개수</li>" +
            "</ul>" +
            "<hr/>" +
            "<h2>Response</h2>" +
            "<ul>" +
                "<li>popularScore : 게시글 인기 점수 (좋아요 * 0.7 + 조회수 * 0.3) </li>" +
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
                "<li>content : 게시글 내용(path=mobile 일 경우만 나옴)</li>" +
                "<br>"+
                "<li>hasNext : 다음 게시글 조회 가능 여부</li>" +
                "<li>hasPrevious : 이전 게시글 조회 가능 여부</li>" +
                "<li>totalPages : 총 게시글 개수</li>" +
                "<li>currentPage : 현재 페이지 번호</li>" +
            "</ul>" +
            "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200")
    })
    public ResponseEntity<CommonResponse<PaginatedResponse<List<CommunityPostListResDTO>>>> getCommunityPostList(
            @RequestParam(required = false) String postType,
            @RequestParam(required = false, defaultValue = "recent") String order,
            @RequestParam(required = false, defaultValue = "web") String path,
            @RequestParam(required = false) String baseTime,
            @RequestParam(required = false, defaultValue = "1") String page,
            @RequestParam(required = false, defaultValue = "12") String size,
            @RequestParam(required = false) String lastNo,
            @RequestParam(required = false) String popularScore,
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
            "<li>likeCount : 게시글 좋아요 개수</li>" +
            "<li>commentCount : 총 댓글 개수(댓글 수+답글 수)</li>" +
            "<li>createdAt : 게시글 생성+ 일시</li>" +
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
