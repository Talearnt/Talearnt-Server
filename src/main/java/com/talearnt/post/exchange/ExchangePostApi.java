package com.talearnt.post.exchange;

import com.talearnt.post.exchange.request.ExchangePostReqDTO;
import com.talearnt.user.talent.response.MyTalentsResDTO;
import com.talearnt.util.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface ExchangePostApi {


    @Operation(summary = "게시물 작성 페이지 - 주고 싶은 재능 불러 오기",
            description = "<h2>내용</h2>" +
                    "<p>재능 게시글 등록 페이지에서 호출할 내용입니다.<p>" +
                    "<p>주고 싶은 재능에 들어갈 내용입니다.</p>" +
                    "<hr>" +
                    "<p>재능 키워드 중 활성화 되어 있는 것들만 가져옵니다.</p>" +
                    "<p>과거 활성화된 재능 키워드를 등록했으나 현재는 사용하지 않는 재능 키워드가 나의 재능에 등록되어 있을 경우</p>" +
                    "<p>비활성화된 키워드를 제외하고 가져옵니다.</p>" +
                    "<p>List 길이가 0일 수도 있습니다.</p>" +
                    "<h2>Response</h2>" +
                    "<ul>" +
                        "<li><strong>myTalentNo :</strong> 나의 재능 번호</li>" +
                        "<li><strong>talentName :</strong> 재능 이름</li>" +
                    "</ul>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", ref = "EXPIRED_TOKEN"),
            @ApiResponse(responseCode = "403", ref = "ACCESS_DENIED")
    })
    public ResponseEntity<CommonResponse<List<MyTalentsResDTO>>> getWantGiveMyTalentsForPost(Authentication auth);


    @Operation(summary = "게시물 등록",
            description = "<h2>내용</h2>" +
                    "<p>재능 게시물 등록입니다. ( S3 미구현 - 추후 구현 )<p>" +
                    "<hr/>"+
                    "<h2>선행 필수 내용</h2>"+
                    "<p>로그인을 하지 않으면 오류가 발생합니다.</p>"+
                    "<p>키워드 등록을 하지 않으면 오류가 발생합니다. (나의 주고 싶은 재능 등록 필요)</p>"+
                    "<p>아래는 Enum Class 및 Regex 규칙이 적용되었습니다.</p>"+
                    "<p>ExchangeType : 온라인,오프라인,온/오프라인 이렇게 보내주셔야 합니다.</p>"+
                    "<p>Duration : 기간 미정,1개월,2개월,3개월,3개월 이상 이렇게 보내주셔야 합니다.</p>"
                    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", ref = "EXPIRED_TOKEN"),
            @ApiResponse(responseCode = "403", ref = "ACCESS_DENIED"),
            @ApiResponse(responseCode = "404-1", ref = "POST_GIVE_MY_TALENT_NOT_FOUND"),
            @ApiResponse(responseCode = "404-2", ref = "KEYWORD_CATEGORY_NOT_FOUND"),
            @ApiResponse(responseCode = "400-1", ref = "POST_TITLE_LENGTH_MISSING"),
            @ApiResponse(responseCode = "400-2", ref = "POST_CONTENT_MIN_LENGTH"),
            @ApiResponse(responseCode = "400-3", ref = "POST_KEYWORD_LENGTH_MISSING"),
            @ApiResponse(responseCode = "400-4", ref = "POST_KEYWORD_LENGTH_OVER"),
            @ApiResponse(responseCode = "400-5", ref = "POST_BAD_REQUEST"),
            @ApiResponse(responseCode = "400-6", ref = "POST_DURATION_MISSING"),
    })
    public ResponseEntity<CommonResponse<String>> writeExchangePost(@RequestBody @Valid ExchangePostReqDTO exchangePostReqDTO);
}
