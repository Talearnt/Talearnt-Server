package com.talearnt.join;

import com.talearnt.enums.ErrorCode;
import com.talearnt.enums.Regex;
import com.talearnt.join.request.JoinReqDTO;
import com.talearnt.join.request.KakaoJoinReqDTO;
import com.talearnt.util.exception.CustomException;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.valid.DynamicValid;
import com.talearnt.util.version.RestControllerV1;
import com.talearnt.verification.VerificationReqDTO;
import com.talearnt.verification.VerificationService;
import com.talearnt.verification.VerifyCodeReqDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "1. 회원가입", description = "이용 약관 추가, 카카오톡 회원가입 미구현")
@Log4j2
@RestControllerV1
@RequiredArgsConstructor
@Validated
public class JoinController {

    private final JoinService joinService;
    private final VerificationService verificationService;

    @Operation(summary = "아이디 중복 확인 - 중복O True, 중복X False")
    @GetMapping("/join/users/{userId}")
    @ApiResponse(responseCode = "400", ref = "USER_ID_NOT_EMAIL_FORMAT")
    public ResponseEntity<CommonResponse<Boolean>> checkDuplicatedUserID(@PathVariable @DynamicValid(errorCode = ErrorCode.USER_ID_NOT_EMAIL_FORMAT, pattern = Regex.EMAIL)
                                                                             String userId){
        return joinService.checkDuplicatedUserId(userId);
    }

    @Operation(summary = "휴대폰 인증까지 마치고 회원 가입 완료 단계, 자사 회원 가입",
            description = "회원가입 인증과 모든 것을 마치고 회원가입 완료를 눌렀을 때 호출<br> 필수 이용약관은 true로 보내셔야 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400-1", ref = "UNVERIFIED_AUTH_CODE"),
            @ApiResponse(responseCode = "400-2", ref = "USER_ID_NOT_EMAIL_FORMAT"),
            @ApiResponse(responseCode = "400-3", ref = "USER_PASSWORD_PATTERN_MISMATCH"),
            @ApiResponse(responseCode = "400-4", ref = "USER_PASSWORD_MISSING"),
            @ApiResponse(responseCode = "400-5", ref = "USER_GENDER_MISSMATCH"),
            @ApiResponse(responseCode = "400-6", ref = "USER_PHONE_NUMBER_FORMAT_MISMATCH"),
            @ApiResponse(responseCode = "400-7", ref = "DUPLICATE_USER_ID"),
            @ApiResponse(responseCode = "400-8", ref = "USER_REQUIRED_NOT_AGREE"),
            @ApiResponse(responseCode = "404", ref = "USER_NOT_FOUND_AGREE"),
    })
    @PostMapping("/join")
    public ResponseEntity<CommonResponse<String>> addUser(@RequestBody @Valid JoinReqDTO joinReqDTO) throws CustomException {
        return joinService.registerUser(joinReqDTO);
    }


    @Operation(summary = "카카오톡 회원가입",description = "필수 이용약관은 true로 보내셔야 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카카오톡 회원가입 성공"),
            @ApiResponse(responseCode = "400-1", ref = "UNVERIFIED_AUTH_CODE"),
            @ApiResponse(responseCode = "400-2", ref = "USER_ID_NOT_EMAIL_FORMAT"),
            @ApiResponse(responseCode = "400-3", ref = "USER_GENDER_MISSMATCH"),
            @ApiResponse(responseCode = "400-4", ref = "USER_PHONE_NUMBER_FORMAT_MISMATCH"),
            @ApiResponse(responseCode = "400-5", ref = "DUPLICATE_USER_ID"),
            @ApiResponse(responseCode = "400-6", ref = "USER_REQUIRED_NOT_AGREE"),
            @ApiResponse(responseCode = "404", ref = "USER_NOT_FOUND_AGREE"),
    })
    @PostMapping("/join/kakao")
    public ResponseEntity<CommonResponse<String>> addKakaoUser(@RequestBody @Valid KakaoJoinReqDTO kakaoJoinReqDTO) throws CustomException {
        return joinService.addKakaoUser(kakaoJoinReqDTO);
    }

    @Operation(summary = "인증 문자 메세지 발송",
            description = "UserId와 휴대폰 번호를 입력하면, 인증 문자 발송")
    @PostMapping("/join/sms")    // 인증 문자메세지 발송
    public ResponseEntity<CommonResponse<String>> sendSMS(@RequestBody VerificationReqDTO verificationReqDTO){
        return verificationService.sendVerificationMessage(verificationReqDTO);
    }

    @Operation(summary = "발송된 인증 메세지의 코드 확인",
            description = "인증 번호를 검증합니다.")
    @PostMapping("/join/verifyCode")    // User가 입력한 code 검증
    public ResponseEntity<CommonResponse<Boolean>> verifyCode(@RequestBody VerifyCodeReqDTO verifyCodeReqDTO){
        return verificationService.verifyCode(verifyCodeReqDTO);
    }
}
