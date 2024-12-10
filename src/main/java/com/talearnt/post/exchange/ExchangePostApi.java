package com.talearnt.post.exchange;

import com.talearnt.user.talent.response.MyTalentsResDTO;
import com.talearnt.util.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

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

}
