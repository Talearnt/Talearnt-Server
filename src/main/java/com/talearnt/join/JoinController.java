package com.talearnt.join;

import com.talearnt.enums.ErrorCode;
import com.talearnt.util.exception.CustomException;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.version.RestControllerV1;
import com.talearnt.verification.VerificationReqDTO;
import com.talearnt.verification.VerificationService;
import com.talearnt.verification.VerifyCodeReqDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "JoinController : 회원가입", description = "회원가입 관련")
@Log4j2
@RestControllerV1
@RequiredArgsConstructor
public class JoinController {

    private final JoinService joinService;
    private final VerificationService verificationService;

    @Operation(summary = "휴대폰 인증까지 마치고 회원 가입 완료 단계",
            description = "회원가입 인증과 모든 것을 마치고 회원가입 완료를 눌렀을 때 호출")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", ref = "UNVERIFIED_AUTH_CODE"),
            @ApiResponse(responseCode = "400", ref = "USER_ID_NOT_EMAIL_FORMAT"),
            @ApiResponse(responseCode = "400", ref = "USER_PASSWORD_PATTERN_MISMATCH"),
            @ApiResponse(responseCode = "400", ref = "USER_PASSWORD_MISSING"),
            @ApiResponse(responseCode = "400", ref = "USER_GENDER_MISSMATCH"),
            @ApiResponse(responseCode = "400", ref = "USER_PHONE_NUMBER_FORMAT_MISMATCH"),
    })
    @PostMapping("/join")
    // 회원 등록
    public ResponseEntity<CommonResponse<String>> addUser(@RequestBody JoinReqDTO joinReqDTO) throws CustomException {
        return joinService.registerUser(joinReqDTO);
    }

    @PostMapping("/join/kakao")
    // 회원 등록
    public ResponseEntity<CommonResponse<String>> addKakaoUser(@RequestBody JoinReqDTO joinReqDTO) throws CustomException {
        if(verificationService.isVerifiedCheck(joinReqDTO)){
            joinService.registerUser(joinReqDTO);
            return CommonResponse.success(new String("회원가입 성공"));
        }else {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
    }




    @PostMapping("/sendSMS")    // 인증 문자메세지 발송
    public void sendSMS(@RequestBody VerificationReqDTO verificationReqDTO){
        String verificationCode = Integer.toString((int)(Math.random() * (9999 - 1000 + 1)) + 1000);
        verificationReqDTO.setVerificationCode(verificationCode);
        verificationService.sendVerificationMessage(verificationReqDTO);
    }


    @PostMapping("/verifyCode")    // User가 입력한 code 검증
    public boolean verifyCode(@RequestBody VerifyCodeReqDTO verifyCodeReqDTO){
        return verificationService.verifyCode(verifyCodeReqDTO);
    }
}
