package com.talearnt.post.exchange;


import com.talearnt.examples.RestControllerV1;
import com.talearnt.post.exchange.request.ExchangePostReqDTO;
import com.talearnt.post.exchange.response.ExchangePostReadResDTO;
import com.talearnt.post.service.PostService;
import com.talearnt.util.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "재능 교환 게시글")
@RestControllerV1
@Log4j2
@RequiredArgsConstructor
public class ExchangePostController {

    private final PostService exchangePostServiceImpl;

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

    @GetMapping("/exchange-posts/{exchangePostNo}")
    public ResponseEntity<CommonResponse<ExchangePostReadResDTO>> readPost(@PathVariable Long exchangePostNo){
        return exchangePostServiceImpl.read(exchangePostNo);
    }


    @Operation(summary = "재능 교환 게시글 수정")
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
    @PutMapping("/exchange-posts/{exchangePostNo}")
    public ResponseEntity<CommonResponse<String>> updatePost(@RequestBody @Valid ExchangePostReqDTO dto, @PathVariable Long exchangePostNo){
        return exchangePostServiceImpl.update(dto,exchangePostNo);
    }
}
