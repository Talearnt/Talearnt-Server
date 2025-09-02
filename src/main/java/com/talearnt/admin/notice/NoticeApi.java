package com.talearnt.admin.notice;

import com.talearnt.admin.notice.request.NoticeInsertReqDTO;
import com.talearnt.admin.notice.response.NoticeDetailResDTO;
import com.talearnt.admin.notice.response.NoticeListResDTO;
import com.talearnt.enums.common.ClientPathType;
import com.talearnt.util.common.ClientPath;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.response.PaginatedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface NoticeApi {

    @Operation(summary = "공지사항 목록"
            , description = "<h2>내용</h2>" +
            "<p>공지사항 목록을 조회합니다.</p>" +
            "<p>웹과 모바일의 Response에 차이가 있습니다. 웹은 content가 포함됩니다.</p>" +
            "<p>path 는 헤더 X-Client-Path 에 담으세요. 기본 값은 WEB 입니다.</p>" +
            "<hr/>" +
            "<h2>Response - 공통</h2>" +
            "<ul>" +
            "<li>noticeNo : 공지사항 번호</li>" +
            "<li>title : 제목</li>" +
            "<li>noticeType : 공지 유형</li>" +
            "<li>createdAt : 생성일시</li>" +
            "</ul>" +
            "<h2>Response - 웹에서 추가</h2>" +
            "<ul>" +
            "<li>content : 공지사항 본문</li>" +
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
            @ApiResponse(responseCode = "200")
    })
    public ResponseEntity<CommonResponse<PaginatedResponse<List<NoticeListResDTO>>>> getNoticeList(
            @ClientPath ClientPathType path,
            @RequestParam(value = "page", required = false, defaultValue = "1") String page,
            @RequestParam(value = "size", required = false, defaultValue = "15") String size
    );


    @Operation(summary = "공지사항 상세 보기",
            description = "<h2>내용</h2>" +
                    "<p>공지사항 상세 정보를 조회합니다.</p>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", ref = "POST_NOT_FOUND")
    })
    public ResponseEntity<CommonResponse<NoticeDetailResDTO>> getNoticeDetail(@PathVariable Long noticeNo);

    @Operation(summary = "공지사항 작성",
            description = "<h2>내용</h2>" +
                    "<p>공지사항을 작성합니다.</p>" +
                    "<p>로그인 필수이며 관리자 이상의 권한이 필요합니다.</p>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", ref = "EXPIRED_TOKEN"),
            @ApiResponse(responseCode = "403", ref = "ACCESS_DENIED")
    })
    public ResponseEntity<CommonResponse<Void>> createNotice(@RequestBody NoticeInsertReqDTO noticeInsertReqDTO);

}
