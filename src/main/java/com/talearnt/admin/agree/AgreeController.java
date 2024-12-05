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

@Tag(name = "관리자 페이지")
@RestAdminControllerV1
@RequiredArgsConstructor
public class AgreeController implements AgreeApi {

    private final AgreeService agreeService;



    //@PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/agree-codes")
    public ResponseEntity<CommonResponse<String>> addAgreeCode(@RequestBody @Valid AgreeCodeReqDTO agreeCodeReqDTO){
        return agreeService.addAgreeCodeAndAgreeContent(agreeCodeReqDTO);
    }


    @GetMapping("/agree-codes/active")
    public ResponseEntity<CommonResponse<List<AgreeCodeListResDTO>>> getActiveTerms(){
        return agreeService.getAcivatedAgreeCodeList();
    }

}
