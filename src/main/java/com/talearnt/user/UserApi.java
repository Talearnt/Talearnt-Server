package com.talearnt.user;

import com.talearnt.user.infomation.request.TestChangePwdReqDTO;
import com.talearnt.user.talent.request.MyTalentReqDTO;
import com.talearnt.user.talent.response.MyTalentsListResDTO;
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

public interface UserApi {
    @Operation(summary = "테스트용 비밀번호 바꾸기, 실 구현 X", description = "비번은 암호화가 걸려있어 변경이 어렵습니다. 이것으로 비번은 자유롭게 바꿀 수 있지만, Login은 Valid를 하기에 규칙은 지켜서 생성하세요.")
    public ResponseEntity<CommonResponse<String>> changePassword(@RequestBody TestChangePwdReqDTO testChangePwdReqDTO);


    @Operation(summary = "나의 재능, 관심 키워드 추가 (권한 : 유저 이상 - 미구현)",
            description = "<h2>내용</h2>" +
                    "<p>나의 재능 키워드와 관심 키워드를 동시에 입력하는 API입니다.</p>" +
                    "<h2>Body</h2>" +
                    "<ul>" +
                        "<li><strong>giveTalents :</strong> 주고 싶은 키워드 코드들 <strong>조건) 최소 1개 이상, 최대 5개 이하</strong></li>" +
                        "<li><strong>interestTalents :</strong> 관심 있는 키워드 코드들 <strong>조건) 최소 1개 이상, 최대 5개 이하</strong></li>" +
                    "</ul>" +
                    "<p>DB에 존재하지 않은 키워드나 사용하지 않은 과거 키워드를 입력했을 경우 Exception 발생</p>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "성공적으로 나의 재능 및 관심 재능 키워드가 등록되었습니다."),
            @ApiResponse(responseCode = "401-1", ref = "EXPIRED_TOKEN"),
            @ApiResponse(responseCode = "401-2", ref = "INVALID_TOKEN"),
            @ApiResponse(responseCode = "400-1", ref = "POST_REQUEST_MISSING"),
            @ApiResponse(responseCode = "400-2", ref = "POST_KEYWORD_LENGTH_MISSING"),
            @ApiResponse(responseCode = "404", ref = "KEYWORD_CATEGORY_NOT_FOUND"),
    })
    public ResponseEntity<CommonResponse<String>> addMyTalents(@RequestBody @Valid MyTalentReqDTO talents);

}
