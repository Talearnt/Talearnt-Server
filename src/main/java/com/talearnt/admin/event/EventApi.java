package com.talearnt.admin.event;

import com.talearnt.admin.event.response.EventListResDTO;
import com.talearnt.enums.common.ClientPathType;
import com.talearnt.util.common.ClientPath;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.response.PaginatedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface EventApi {

    @Operation(summary = "이벤트 목록"
            , description = "<h2>내용</h2>" +
            "<p>이벤트 목록을 조회합니다.</p>" +
            "<p>path 는 헤더 X-Client-Path 에 담으세요. 기본 값은 WEB 입니다.</p>" +
            "<p>이벤트 목록은 진행중인 것이 먼저 옵니다.</p>" +
            "<hr/>" +
            "<h2>Response - 공통</h2>" +
            "<ul>" +
                "<li>eventNo : 이벤트 번호</li>" +
                "<li>bannerUrl : 배너 이미지 URL</li>" +
                "<li>startDate : 시작일시</li>" +
                "<li>endDate : 종료일시</li>" +
                "<li>isActive : 진행중 여부</li>" +
            "</ul>" +
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
    ResponseEntity<CommonResponse<PaginatedResponse<List<EventListResDTO>>>> getEventList(
            @ClientPath ClientPathType path,
            @RequestParam(value = "page", required = false, defaultValue = "1") String page,
            @RequestParam(value = "size", required = false, defaultValue = "15") String size
    );

}
