package com.talearnt.user;

import com.talearnt.comment.community.response.MyCommentsResDTO;
import com.talearnt.enums.common.ClientPathType;
import com.talearnt.post.community.response.CommunityPostListResDTO;
import com.talearnt.post.exchange.response.ExchangePostListResDTO;
import com.talearnt.user.infomation.request.ProfileReqDTO;
import com.talearnt.user.infomation.request.TestChangePwdReqDTO;
import com.talearnt.user.infomation.response.UserActivityCountsResDTO;
import com.talearnt.user.infomation.response.UserHeaderResDTO;
import com.talearnt.user.talent.request.MyTalentReqDTO;
import com.talearnt.util.common.ClientPath;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.response.PaginatedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface UserApi {
    @Operation(summary = "테스트용 비밀번호 바꾸기, 실 구현 X", description = "비번은 암호화가 걸려있어 변경이 어렵습니다. 이것으로 비번은 자유롭게 바꿀 수 있지만, Login은 Valid를 하기에 규칙은 지켜서 생성하세요.")
    public ResponseEntity<CommonResponse<String>> changePassword(@RequestBody TestChangePwdReqDTO testChangePwdReqDTO);


    @Operation(summary = "사용자 기본 정보 (Keyword 설정 여부)",
            description = "<h2>내용</h2>" +
                    "<p>디자인 페이지의 Header에서 사용할 유저의 기본 정보입니다.</p>" +
                    "<p><strong>로그인 후 이용 가능합니다.</strong></p>" +
                    "<h2>Response</h2>" +
                    "<ul>" +
                    "<li>userNo : 로그인한 유저 번호 - 본인 게시글 판단 여부 및 API 호출 용도</li>" +
                    "<li>userId : 로그인한 유저 아이디 - 이메일 형식 아이디</li>" +
                    "<li>profileImg : 유저의 프로필 이미지 경로</li>" +
                    "<li>nickname : 유저의 닉네임</li>" +
                    "<li>giveTalents : 유저 주고 싶은 재능 코드</li>" +
                    "<li>receiveTalents : 유저 받고 싶은 재능 코드</li>" +
                    "</ul>" +
                    "<p>나의 재능 등록만 해서 Test 아이디로 조회할 경우 중복 값 및 5개 이상 여러 값이 들어가 있습니다.</p>"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", ref = "EXPIRED_TOKEN")
    })
    public ResponseEntity<CommonResponse<UserHeaderResDTO>> getHeaderUserInfo(Authentication authentication);


    @Operation(summary = "나의 재능, 관심 키워드 추가 (권한 : 유저 이상 - 미구현)",
            description = "<h2>내용</h2>" +
                    "<p>나의 재능 키워드와 관심 키워드를 동시에 입력하는 API입니다.</p>" +
                    "<h2>Body</h2>" +
                    "<ul>" +
                    "<li><strong>giveTalents :</strong> 주고 싶은 키워드 코드들 <strong>조건) 최소 1개 이상, 최대 5개 이하</strong></li>" +
                    "<li><strong>receiveTalents :</strong> 관심 있는 키워드 코드들 <strong>조건) 최소 1개 이상, 최대 5개 이하</strong></li>" +
                    "</ul>" +
                    "<p>DB에 존재하지 않은 키워드나 사용하지 않은 과거 키워드를 입력했을 경우 Exception 발생</p>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 나의 재능 및 관심 재능 키워드가 등록되었습니다."),
            @ApiResponse(responseCode = "401-1", ref = "EXPIRED_TOKEN"),
            @ApiResponse(responseCode = "401-2", ref = "INVALID_TOKEN"),
            @ApiResponse(responseCode = "400-1", ref = "POST_REQUEST_MISSING"),
            @ApiResponse(responseCode = "400-2", ref = "POST_KEYWORD_LENGTH_MISSING"),
            @ApiResponse(responseCode = "404", ref = "KEYWORD_CATEGORY_NOT_FOUND"),
    })
    public ResponseEntity<CommonResponse<String>> addMyTalents(@RequestBody @Valid MyTalentReqDTO talents);


    @Operation(summary = "유저 정보 수정",
            description = "<h2>내용</h2>" +
                    "<p>유저의 정보를 수정합니다.</p>" +
                    "<p>닉네임이 중복될 경우 Exception이 발생합니다.</p>" +
                    "<p>반환 값은 getHeaderUserInfo가 반환됩니다. 헤더에 셋팅될 값을 변경하면 됩니다.</p>" +
                    "<p>불필요할 경우 말해주시면 반환 값을 없애 리소스를 줄이도록 하겠습니다.</p>" +
                    "<h2>Request</h2>" +
                    "<ul>" +
                        "<li>nickname : 유저의 닉네임</li>" +
                        "<li>profileImg : 유저의 프로필 이미지 경로</li>" +
                        "<li>giveTalents : 주고 싶은 키워드 코드들 <strong>조건) 최소 1개 이상, 최대 5개 이하</strong></li>" +
                        "<li>receiveTalents : 관심 있는 키워드 코드들 <strong>조건) 최소 1개 이상, 최대 5개 이하</strong></li>" +
                    "</ul>"+
                    "<h2>Response</h2>" +
                    "<ul>" +
                        "<li>userNo : 로그인한 유저 번호 - 본인 게시글 판단 여부 및 API 호출 용도</li>" +
                        "<li>userId : 로그인한 유저 아이디 - 이메일 형식 아이디</li>" +
                        "<li>profileImg : 유저의 프로필 이미지 경로</li>" +
                        "<li>nickname : 유저의 닉네임</li>" +
                        "<li>giveTalents : 유저 주고 싶은 재능 코드</li>" +
                        "<li>receiveTalents : 유저 받고 싶은 재능 코드</li>"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", ref = "EXPIRED_TOKEN"),
            @ApiResponse(responseCode = "404", ref = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "400", ref = "DUPLICATE_USER_NICKNAME")
    })
    public ResponseEntity<CommonResponse<UserHeaderResDTO>> updateMyInfo(@RequestBody @Valid ProfileReqDTO profileReqDTO);


    @Operation(summary = "나의 활동 정보 조회",
            description = "<h2>내용</h2>" +
                    "<p>나의 활동 정보를 조회합니다.</p>" +
                    "<h2>Response</h2>" +
                    "<ul>" +
                        "<li>favoritePostCount : 내가 찜한 게시글 수</li>" +
                        "<li>myPostCount : 내가 작성한 재능 교환 게시글 + 커뮤니티 게시글 수</li>" +
                        "<li>myCommentCount : 내가 작성한 댓글 + 답글 수</li>" +
                    "</ul>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", ref = "EXPIRED_TOKEN"),
    })
    public ResponseEntity<CommonResponse<UserActivityCountsResDTO>> getMyActivityCounts(Authentication authentication);



    @Operation(summary = "내가 작성한 재능교환 게시글 목록"
            , description = "<h2>내용</h2>" +
            "<p>로그인한 사용자가 작성한 재능교환 게시글 목록을 조회합니다.</p>" +
            "<p>웹과 모바일의 Response에 차이가 있습니다. 모바일은 lastNo 기반 페이징, 웹은 page 기반 페이징입니다.</p>" +
            "<p>path에 (mobile|web) 으로 보내주셔야 하며, 기본값은 web입니다.</p>" +
            "<p>order는 (recent|popular)로 구성되어 있고, 기본 값은 recent입니다.</p>" +
            "<hr/>" +
            "<h2>Response - 공통</h2>" +
            "<ul>" +
            "<li>profileImg : 작성자의 프로필 이미지 경로</li>" +
            "<li>nickname : 작성자의 닉네임</li>" +
            "<li>authority : 작성자의 권한</li>" +
            "<li>exchangePostNo : 게시글 번호</li>" +
            "<li>status : 게시글 모집 상태</li>" +
            "<li>exchangeType : 게시글 진행 방식</li>" +
            "<li>duration : 게시글 진행 기간</li>" +
            "<li>title : 게시글 제목</li>" +
            "<li>content : 게시글 내용</li>" +
            "<li>giveTalents : 주고 싶은 재능</li>" +
            "<li>receiveTalents : 받고 싶은 재능</li>" +
            "<li>createdAt : 게시글 작성일</li>" +
            "<li>count : 게시글 조회수</li>" +
            "<li>openedChatRoomCount : 신청된 채팅방 갯수</li>" +
            "<li>favoriteCount : 게시글 찜 갯수</li>" +
            "<li>isFavorite : 게시글 찜 여부</li>" +
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
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", ref = "EXPIRED_TOKEN")
    })
    ResponseEntity<CommonResponse<PaginatedResponse<List<ExchangePostListResDTO>>>> getMyExchangePostList(
            @RequestParam(value = "page", required = false, defaultValue = "1") String page,
            @RequestParam(value = "size", required = false, defaultValue = "15") String size,
            @RequestParam(value = "lastNo", required = false) String lastNo,
            @Schema(hidden = true) @ClientPath ClientPathType path,
            Authentication auth
    );

    @Operation(summary = "내가 찜한 재능 교환 게시글 목록"
            , description = "<h2>내용</h2>" +
            "<p>로그인한 사용자가 찜한 재능 교환 게시글 목록을 조회합니다.</p>" +
            "<p>path 는 헤더 X-Client-Path 에 담으세요. 기본 값은 WEB 입니다.</p>" +
            "<hr/>" +
            "<h2>Response - 공통</h2>" +
            "<ul>" +
            "<li>profileImg : 작성자 프로필 이미지</li>" +
            "<li>nickname : 작성자 닉네임</li>" +
            "<li>authority : 작성자 권한</li>" +
            "<li>exchangePostNo : 게시글 번호</li>" +
            "<li>status : 모집 상태</li>" +
            "<li>exchangeType : 진행 방식</li>" +
            "<li>duration : 진행 기간</li>" +
            "<li>requiredBadge : 인증 뱃지 필요 여부</li>" +
            "<li>title : 게시글 제목</li>" +
            "<li>content : 게시글 내용(100자 이내)</li>" +
            "<li>giveTalents : 주고 싶은 재능 목록</li>" +
            "<li>receiveTalents : 받고 싶은 재능 목록</li>" +
            "<li>createdAt : 게시글 작성일</li>" +
            "<li>count : 조회수</li>" +
            "<li>openedChatRoomCount : 신청된 채팅방 개수</li>" +
            "<li>favoriteCount : 찜 개수</li>" +
            "<li>isFavorite : 찜 여부(항상 true)</li>" +
            "</ul>" +
            "<hr/>" +
            "<h2>pagination - Mobile</h2>" +
            "<ul>" +
            "<li>hasNext : 다음 페이지 이동 가능 여부</li>" +
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
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", ref = "EXPIRED_TOKEN")
    })
    public ResponseEntity<CommonResponse<PaginatedResponse<List<ExchangePostListResDTO>>>> getFavoriteExchanges(@RequestParam(required = false, defaultValue = "1") String page,
                                                                                                                @RequestParam(required = false, defaultValue = "15") String size,
                                                                                                                @ClientPath ClientPathType path,
                                                                                                                Authentication auth);

    @Operation(summary = "내가 작성한 커뮤니티 게시글 목록"
            , description = "<h2>내용</h2>" +
            "<p>로그인한 사용자가 작성한 커뮤니티 게시글 목록을 조회합니다.</p>" +
            "<p>웹과 모바일의 Response에 차이가 있습니다. Mobile에는 content가 추가됩니다.</p>" +
            "<p>path에 (mobile|web) 으로 보내주셔야 하며, 기본값은 web입니다.</p>" +
            "<p>order는 (recent|hot)으로 구성되어 있고, 기본 값은 recent입니다.</p>" +
            "<p>postType은 나중에 내가 작성한 게시글 목록에도 필터 기능이 생길 가능성을 염두에 두어 추가해 두었습니다.</p>" +
            "<hr/>" +
            "<h2>Response - 공통</h2>" +
            "<ul>" +
            "<li>profileImg : 작성자의 프로필 이미지 경로</li>" +
            "<li>nickname : 작성자의 닉네임</li>" +
            "<li>authority : 작성자의 권한</li>" +
            "<li>communityPostNo : 게시글 번호</li>" +
            "<li>postType : 커뮤니티 게시글 타입</li>" +
            "<li>title : 게시글 제목</li>" +
            "<li>count : 게시글 조회수</li>" +
            "<li>commentCount : (댓글 수+답글 수) 개수</li>" +
            "<li>likeCount : 게시글 좋아요 개수</li>" +
            "<li>isLike : 게시글 좋아요 여부</li>" +
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
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", ref = "EXPIRED_TOKEN")
    })
    ResponseEntity<CommonResponse<PaginatedResponse<List<CommunityPostListResDTO>>>> getMyCommunityPostList(
            @RequestParam(required = false) String postType,
            @RequestParam(required = false, defaultValue = "recent") String order,
            @Schema(hidden = true) @ClientPath ClientPathType path,
            @RequestParam(required = false, defaultValue = "1") String page,
            @RequestParam(required = false, defaultValue = "12") String size,
            @RequestParam(required = false) String lastNo,
            Authentication authentication);



    @Operation(summary = "내가 작성한 커뮤니티 댓글 목록"
            , description = "<h2>내용</h2>" +
            "<p>로그인한 사용자가 작성한 커뮤니티 댓글 목록을 조회합니다.</p>" +
            "<p>path에 (mobile|web) 으로 보내주셔야 하며, 기본값은 web입니다.</p>" +
            "<hr/>" +
            "<h2>Request</h2>" +
            "<ul>" +
                "<li>path : 호출 경로 (web/mobile) <b>필수</b></li>" +
                "<li>lastNo : 마지막 댓글 번호(모바일 페이징용, 모바일만 사용)</li>" +
                "<li>page : 페이지 번호(모바일은 무조건 1)</li>" +
                "<li>size : 페이지당 데이터 개수(기본 30)</li>" +
            "</ul>" +
            "<hr/>" +
            "<h2>Response - 공통</h2>" +
            "<ul>" +
                "<li>postNo : 커뮤니티 게시글 번호</li>" +
                "<li>postType : 게시글 타입</li>" +
                "<li>postTitle : 게시글 제목</li>" +
                "<li>commentNo : 댓글 번호</li>" +
                "<li>commentContent : 댓글 내용</li>" +
                "<li>commentCreatedAt : 댓글 작성일</li>" +
                "<li>commentUpdatedAt : 댓글 수정일</li>" +
            "</ul>" +
            "<hr/>" +
            "<h2>pagination - Mobile</h2>" +
            "<ul>" +
            "<li>hasNext : 다음 데이터 이동 가능 여부</li>" +
            "</ul>" +
            "<h2>pagination - Web</h2>" +
            "<ul>" +
                "<li>hasNext - 다음 페이지 이동 가능 여부</li>" +
                "<li>hasPrevious - 이전 페이지 이동 가능 여부</li>" +
                "<li>totalCount - 총 데이터 개수</li>" +
                "<li>totalPages - 총 페이지 개수</li>" +
                "<li>currentPage - 현재 페이지 번호</li>" +
                "<li>latestCreatedAt - 가장 최근 Data 작성일</li>" +
            "</ul>"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", ref = "EXPIRED_TOKEN")
    })
    ResponseEntity<CommonResponse<PaginatedResponse<List<MyCommentsResDTO>>>> getMyComments(
            @RequestParam(required = false, defaultValue = "1") String page,
            @RequestParam(required = false, defaultValue = "15") String size,
            @RequestParam(required = false) String lastNo,
            @Schema(hidden = true) @ClientPath ClientPathType path,
            Authentication authentication
    );


}
