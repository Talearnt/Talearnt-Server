package com.talearnt.user;


import com.talearnt.enums.ErrorCode;
import com.talearnt.enums.Regex;
import com.talearnt.user.reponse.UserFindIdResDTO;
import com.talearnt.user.request.CheckUserVerificationCodeReqDTO;
import com.talearnt.user.request.TestChangePwdReqDTO;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.valid.DynamicValid;
import com.talearnt.util.version.RestControllerV1;
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

import java.util.List;

@Tag(name = "3. 유저",description = "아이디/비밀번호 찾기")
@RestControllerV1
@RequiredArgsConstructor
@Log4j2
@Validated
public class UserController {

    private final UserService userService;
    private final UserFindService findService;

    @Operation(summary = "테스트용 비밀번호 바꾸기, 실 구현 X", description = "비번은 암호화가 걸려있어 변경이 어렵습니다. 이것으로 비번은 자유롭게 바꿀 수 있지만, Login은 Valid를 하기에 규칙은 지켜서 생성하세요.")
    @PostMapping("/users/password/test")
    public ResponseEntity<CommonResponse<String>> changePassword(@RequestBody TestChangePwdReqDTO testChangePwdReqDTO){
        return userService.changeTestPwd(testChangePwdReqDTO);
    }

    @Operation(summary = "아이디 찾기 1. 인증 번호 전송" ,description = "유저의 번호를 가지고, 인증 번호를 만들어 문자 전송")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상 접수(이통사로 접수 예정)"),
            @ApiResponse(responseCode = "400", ref = "USER_PHONE_NUMBER_FORMAT_MISMATCH"),
            @ApiResponse(responseCode = "404", ref = "USER_NOT_FOUND_PHONE_NUMBER"),
    })
    @GetMapping("/users/{phone}/verification-code")
    public ResponseEntity<CommonResponse<String>> sendAuthenticationCode(@PathVariable @DynamicValid(errorCode = ErrorCode.USER_PHONE_NUMBER_FORMAT_MISMATCH,pattern = Regex.PHONE_NUMBER)
                                                                             String phone){
        return findService.sendAuthenticationCode(phone);
    }

    @Operation(summary = "아이디 찾기 2. 인증 번호 검증",description = "휴대폰 번호와 인증 번호를 넘기면 검증 시작")
    @PostMapping("/users/verification-code/validation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "true"),
            @ApiResponse(responseCode = "429", ref = "AUTH_TOO_MANY_REQUEST"),
            @ApiResponse(responseCode = "404", ref = "AUTH_NOT_FOUND_PHONE_CODE"),
            @ApiResponse(responseCode = "400-1", ref = "INVALID_AUTH_CODE"),
            @ApiResponse(responseCode = "400-2", ref = "USER_PHONE_NUMBER_FORMAT_MISMATCH"),
            @ApiResponse(responseCode = "400-3", ref = "AUTH_CODE_FORMAT_MISMATCH"),
    })
    public ResponseEntity<CommonResponse<Boolean>> checkVerificationCode(@RequestBody @Valid CheckUserVerificationCodeReqDTO checkUserVerificationCodeReqDTO){
        return findService.checkVerificationCode(checkUserVerificationCodeReqDTO);
    }

    @Operation(summary = "아이디 찾기 3. 성공",description = "여기 페이지에서 뒤로 가기 버튼 클릭 시 API 작동 안하도록 막기 아니면 로그인 페이지로 이동")
    @PostMapping("/users/user-id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", ref = "AUTH_NOT_FOUND_PHONE_CODE"),
            @ApiResponse(responseCode = "400-1", ref = "USER_PHONE_NUMBER_FORMAT_MISMATCH"),
            @ApiResponse(responseCode = "400-2", ref = "AUTH_CODE_FORMAT_MISMATCH"),
            @ApiResponse(responseCode = "400-3", ref = "UNVERIFIED_AUTH_CODE"),
            @ApiResponse(responseCode = "400-4", ref = "USER_NOT_FOUND_PHONE_NUMBER"),
            @ApiResponse(responseCode = "400-5", ref = "MESSAGE_NOT_RESPONSE"),
    })
    public ResponseEntity<CommonResponse<UserFindIdResDTO>> sendUserIds(@RequestBody @Valid CheckUserVerificationCodeReqDTO checkUserVerificationCodeReqDTO){
        return findService.findUserIds(checkUserVerificationCodeReqDTO);
    }

}
