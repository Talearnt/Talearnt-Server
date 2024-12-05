package com.talearnt.admin.agree;

import com.talearnt.admin.agree.request.AgreeCodeReqDTO;
import com.talearnt.admin.agree.response.AgreeCodeListResDTO;
import com.talearnt.util.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface AgreeApi {
    @Operation(summary = "이용 약관 등록",
            description = "<h2>내용</h2>" +
                    "<p>관리자 전용 페이지에서 사용할 API입니다. (미구현)</p>" +
                    "<h2>Body</h2>" +
                    "<ul>" +
                        "<li><strong>title :</strong> 최소 2글자 이상 입력</li>" +
                        "<li><strong>version :</strong> '숫자.숫자' 형태로 보내야 함 </li>" +
                        "<li><strong>content :</strong> 최소 15글자 이상 입력</li>" +
                        "<li><strong>mandatory :</strong> true - 필수, false - 선택</li>" +
                    "</ul>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이용 약관 등록에 성공하였습니다."),
            @ApiResponse(responseCode = "401", ref = "EXPIRED_TOKEN"),
            @ApiResponse(responseCode = "400-1", ref = "TERMS_TITLE_MISSING"),
            @ApiResponse(responseCode = "400-2", ref = "TERMS_INVALID_VERSION"),
            @ApiResponse(responseCode = "400-3", ref = "TERMS_CONTENT_MISSING"),
    })
    public ResponseEntity<CommonResponse<String>> addAgreeCode(@RequestBody @Valid AgreeCodeReqDTO agreeCodeReqDTO);

    @Operation(summary = "활성화된 이용 약관",
            description = "<h2>내용</h2>" +
                    "<p>활성화된 이용약관 내용입니다.</p>" +
                    "<p>회원가입 완료 버튼 클릭 시 agreeCodeId를 보내주시면 됩니다.</p>" +
                    "<h2>Response</h2>" +
                    "<ul>" +
                        "<li><strong>agreeCodeId :</strong>이용약관 코드 </li>" +
                        "<li><strong> title:</strong> 이용약관 제목</li>" +
                        "<li><strong> content:</strong> 이용약관 내용 </li>" +
                        "<li><strong> version:</strong> 이용약관 버전 </li>" +
                        "<li><strong> mandatory:</strong> 필수 여부 </li>" +
                    "</ul>")
    public ResponseEntity<CommonResponse<List<AgreeCodeListResDTO>>> getActiveTerms();
}
