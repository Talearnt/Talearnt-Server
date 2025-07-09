package com.talearnt.post.favorite;

import com.talearnt.enums.common.ClientPathType;
import com.talearnt.post.exchange.response.ExchangePostListResDTO;
import com.talearnt.util.common.ClientPath;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.response.PaginatedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface FavoriteApi {

    @Operation(summary = "내가 찜한 재능 교환 게시글 목록",
            tags = {"Post-Exchange"}
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



    @Operation(summary = "재능 교환 게시글 찜하기",
            tags = {"Post-Exchange"},
            description = "<h2>내용</h2>" +
                    "<p>재능 교환 게시글 찜하기입니다. 토글 방식으로 작동합니다.</p>" +
                    "<p>비동기 방식으로 작동하기 때문에 좋아요 버튼을 눌렀을 경우 1~2초간 서버에 요청 보내는 것을 제어해야 합니다.</p>" +
                    "<p>제어하지 않을 경우 서버에 부화가 생겨 문제가 발생할 수 있습니다.</p>" +
                    "<p>재능 교환 게시글 찜은 반환값이 없습니다.</p>" +
                    "<p>찜 갯수를 반환할까도 싶었지만, 실시간으로 좋아요 변동이 필요하지 않을 것이라 판단해 프론트 쪽에서 좋아요 갯수를 제어해주시면 될 것 같습니다.</p>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", ref = "EXPIRED_TOKEN"),
            @ApiResponse(responseCode = "404", ref = "POST_NOT_FOUND")
    })
    public ResponseEntity<CommonResponse<Void>> favoriteExchangePost(@PathVariable Long postNo, Authentication auth);
}
