package com.talearnt.post.exchange;


import com.talearnt.post.exchange.request.ExchangePostReqDTO;
import com.talearnt.post.exchange.response.ExchangePostListResDTO;
import com.talearnt.post.exchange.response.ExchangePostReadResDTO;
import com.talearnt.post.service.PostService;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.response.PaginatedResponse;
import com.talearnt.util.version.RestControllerV1;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "재능 교환 게시글 : 잠시 보류", description = "추후에 다시 보완해야해서 이 구조로 만드시면 다시 만드셔야합니다.")
@RestControllerV1
@Log4j2
@RequiredArgsConstructor
public class ExchangePostController {

    private final PostService exchangePostServiceImpl;

    @Operation(summary = "재능 교환 게시글 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "@Param page : 페이지 번호, 기획서에는 몇 개의 Row를 조회하는 지 정의하지 않아 임의로 20개로 정해둔 상태."),
            @ApiResponse(responseCode = "400-1", ref = "PAGE_MIN_NUMBER"),
            @ApiResponse(responseCode = "400-2", ref = "PAGE_OVER_MAX_NUMBER"),
    })
    @GetMapping("/exchange-posts")
    public ResponseEntity<PaginatedResponse<List<ExchangePostListResDTO>>> getPostList(@RequestParam int page){
        return exchangePostServiceImpl.showList(page);
    }


    @Operation(summary = "재능 교환 게시글 등록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "재능 교환 게시글을 등록했습니다."),
            @ApiResponse(responseCode = "401", ref = "EXPIRED_TOKEN"),
            @ApiResponse(responseCode = "400-1", ref = "POST_REQUEST_MISSING"),
            @ApiResponse(responseCode = "400-2", ref = "POST_OVER_REQUEST_LENGTH"),
            @ApiResponse(responseCode = "400-3", ref = "POST_TITLE_OVER_LENGTH"),
            @ApiResponse(responseCode = "400-4", ref = "POST_TITLE_MISSING"),
            @ApiResponse(responseCode = "400-5", ref = "POST_CONTENT_MIN_LENGTH"),
            @ApiResponse(responseCode = "400-6", ref = "POST_CONTENT_MISSING"),
            @ApiResponse(responseCode = "400-7", ref = "POST_BAD_REQUEST"),
    })
    @PostMapping("/exchange-posts")
    public ResponseEntity<CommonResponse<String>> createPost(@RequestBody @Valid ExchangePostReqDTO dto){
        return exchangePostServiceImpl.create(dto);
    }


    @Operation(summary = "재능 교환 게시글 상세보기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "재능 교환 게시글 상세보기."),
            @ApiResponse(responseCode = "404", ref = "POST_NOT_FOUND")
    })
    @GetMapping("/exchange-posts/{exchangePostNo}")
    public ResponseEntity<CommonResponse<ExchangePostReadResDTO>> readPost(@PathVariable Long exchangePostNo){
        return exchangePostServiceImpl.read(exchangePostNo);
    }


    @Operation(summary = "재능 교환 게시글 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "재능 교환 게시글을 수정했습니다."),
            @ApiResponse(responseCode = "401", ref = "EXPIRED_TOKEN"),
            @ApiResponse(responseCode = "400-1", ref = "POST_REQUEST_MISSING"),
            @ApiResponse(responseCode = "400-2", ref = "POST_OVER_REQUEST_LENGTH"),
            @ApiResponse(responseCode = "400-3", ref = "POST_TITLE_OVER_LENGTH"),
            @ApiResponse(responseCode = "400-4", ref = "POST_TITLE_MISSING"),
            @ApiResponse(responseCode = "400-5", ref = "POST_CONTENT_MIN_LENGTH"),
            @ApiResponse(responseCode = "400-6", ref = "POST_CONTENT_MISSING"),
            @ApiResponse(responseCode = "400-7", ref = "POST_BAD_REQUEST"),
            @ApiResponse(responseCode = "400-8", ref = "POST_NOT_FOUND"),
            @ApiResponse(responseCode = "400-9", ref = "POST_ACCESS_DENIED")
    })
    @PutMapping("/exchange-posts/{exchangePostNo}")
    public ResponseEntity<CommonResponse<String>> updatePost(@RequestBody @Valid ExchangePostReqDTO dto, @PathVariable Long exchangePostNo){
        return exchangePostServiceImpl.update(dto,exchangePostNo);
    }


    @Operation(summary = "재능 교환 게시글 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "재능 교환 게시글 상세보기."),
            @ApiResponse(responseCode = "404", ref = "POST_NOT_FOUND"),
            @ApiResponse(responseCode = "401", ref = "EXPIRED_TOKEN"),
            @ApiResponse(responseCode = "403", ref = "POST_ACCESS_DENIED")
    })
    @DeleteMapping("/exchange-posts/{exchangePostNo}")
    public ResponseEntity<CommonResponse<String>> deletePost(@PathVariable Long exchangePostNo, Authentication authentication){
        return exchangePostServiceImpl.delete(exchangePostNo,authentication);
    }
}
