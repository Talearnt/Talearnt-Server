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

    @Operation(summary = "관리자 전용 - 활성화된 이용 약관 (호출X, 하드코딩 O)",
            description = "<h2>내용</h2>" +
                    "<p>활성화된 이용약관 내용입니다.</p>" +
                    "<p><code>해당 API</code> 는 따로 호출 하지 않고, 하드 코딩으로 작업해주시길 바랍니다.</p>" +
                    "<p>이용 약관 제목, 버전, 내용, 등록 일시는 문제 발생 시 증빙서류로 가지고 있어야 한다고 합니다.</p>" +
                    "<p><strong>DB 에는 존재하지만, 회원가입 시 호출하여 등록하지 않고 하드코딩으로 작업해주시길 바랍니다.</strong></p>"+
                    "<h2>관리자 전용 Response</h2>" +
                    "<ul>" +
                        "<li><strong>agreeCodeId :</strong>이용약관 코드 </li>" +
                        "<li><strong> title:</strong> 이용약관 제목</li>" +
                        "<li><strong> content:</strong> 이용약관 내용 </li>" +
                        "<li><strong> version:</strong> 이용약관 버전 </li>" +
                        "<li><strong> mandatory:</strong> 필수 여부 </li>" +
                    "</ul>" +
                    "<hr>" +
                    "<h2>AgreeCodeId 목록</h2>" +
                    "<ul>" +
                        "<li><strong>1</strong> - 이용약관 동의</li>" +
                        "<li><strong>2</strong> - 개인정보 수집 및 이용 동의</li>" +
                        "<li><strong>3</strong> - 마케팅 목적의 개인정보 수집 및 이용 동의</li>" +
                        "<li><strong>4</strong> - 광고성 정보 수신 동의</li>" +
                    "</ul>" +
                    "<p><a target='_blank' href='https://jin02014.atlassian.net/jira/software/projects/TALEARNT/boards/1/timeline?selectedIssue=TALEARNT-68&text=%EC%9D%B4%EC%9A%A9'>이용 약관 문서 보러 가기</a></p>")
    public ResponseEntity<CommonResponse<List<AgreeCodeListResDTO>>> getActiveTerms();
}
