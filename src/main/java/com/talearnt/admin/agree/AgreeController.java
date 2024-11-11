package com.talearnt.admin.agree;

import com.talearnt.admin.agree.request.AgreeCodeReqDTO;
import com.talearnt.admin.agree.response.AgreeCodeListResDTO;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.version.RestAdminControllerV1;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "이용 약관", description = "추가,목록 : O, 보기,수정,삭제 : X")
@RestAdminControllerV1
@RequiredArgsConstructor
public class AgreeController {

    private final AgreeService agreeService;


    @Operation(summary = "이용 약관 등록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이용 약관 등록에 성공하였습니다."),
            @ApiResponse(responseCode = "401", ref = "EXPIRED_TOKEN"),
            @ApiResponse(responseCode = "400-1", ref = "TERMS_TITLE_MISSING"),
            @ApiResponse(responseCode = "400-2", ref = "TERMS_INVALID_VERSION"),
            @ApiResponse(responseCode = "400-3", ref = "TERMS_CONTENT_MISSING"),
    })
    //@PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/agree-codes")
    public ResponseEntity<CommonResponse<String>> addAgreeCode(@RequestBody @Valid AgreeCodeReqDTO agreeCodeReqDTO){
        return agreeService.addAgreeCodeAndAgreeContent(agreeCodeReqDTO);
    }

    @Operation(summary = "활성화된 이용 약관", description = "회원가입 버튼을 눌렀을 때 호출하면 화면에 활성화된 이용 약관이 전송됩니다. 이 이용약관을 토대로 회원 가입할 때 보내주시면 되겠습니다.")
    @GetMapping("/agree-codes/active")
    public ResponseEntity<CommonResponse<List<AgreeCodeListResDTO>>> getActiveTerms(){
        return agreeService.getAcivatedAgreeCodeList();
    }

}
