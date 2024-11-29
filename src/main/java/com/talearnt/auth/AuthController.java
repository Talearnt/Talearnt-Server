package com.talearnt.auth;

import com.talearnt.auth.join.JoinService;
import com.talearnt.enums.common.ErrorCode;
import com.talearnt.enums.common.Regex;
import com.talearnt.auth.join.request.JoinReqDTO;
import com.talearnt.auth.join.request.KakaoJoinReqDTO;
import com.talearnt.auth.login.KakaoLoginService;
import com.talearnt.auth.login.LoginService;
import com.talearnt.auth.login.company.LoginReqDTO;
import com.talearnt.auth.login.kakao.KakaoLoginResDTO;
import com.talearnt.auth.find.AuthFindService;
import com.talearnt.auth.find.reponse.AuthFindResDTO;
import com.talearnt.auth.find.request.CheckUserPwdReqDTO;
import com.talearnt.auth.find.request.CheckUserVerificationCodeReqDTO;
import com.talearnt.auth.find.request.FindByPhoneReqDTO;
import com.talearnt.util.exception.CustomException;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.jwt.TokenResDTO;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.valid.DynamicValid;
import com.talearnt.util.version.RestControllerV1;
import com.talearnt.auth.verification.VerificationReqDTO;
import com.talearnt.auth.verification.VerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth",description = "회원가입/로그인/인증 문자 전송/아이디,비밀번호 찾기")
@RestControllerV1
@RequiredArgsConstructor
@Log4j2
@Validated
public class AuthController {

    private final LoginService loginService;
    private final KakaoLoginService kakaoLoginService;

    //회원 가입
    private final JoinService joinService;
    //회원 가입 전용 인증 문자
    private final VerificationService verificationService;
    //아이디 찾기 전용 인증 문자
    private final AuthFindService findService;

    /*000000000000000000000000000000 회원가입 관련 시작 000000000000000000000000000000*/
    @Operation(summary = "아이디 중복 확인 - 중복O True, 중복X False")
    @ApiResponse(responseCode = "400", ref = "USER_ID_NOT_EMAIL_FORMAT")
    @GetMapping("/auth/users/{userId}")
    public ResponseEntity<CommonResponse<Boolean>> checkDuplicatedUserID(@PathVariable @DynamicValid(errorCode = ErrorCode.USER_ID_NOT_EMAIL_FORMAT, pattern = Regex.EMAIL)
                                                                         String userId){
        return joinService.checkDuplicatedUserId(userId);
    }

    @Operation(summary = "닉네임 중복 확인 - 중복O True, 중복X False")
    @GetMapping("/auth/users/{nickname}")
    public ResponseEntity<CommonResponse<Boolean>> checkDuplicatedNickname(@PathVariable @DynamicValid(errorCode = ErrorCode.DUPLICATE_USER_NICKNAME,pattern = Regex.NICKNAME)
                                                                           String nickname){
        return CommonResponse.success(joinService.checkDuplicatedNickname(nickname));
    }

    @Operation(summary = "랜덤한 닉네임 생성, 회원가입 닉네임 입력창으로 이동 시 호출")
    @GetMapping("/auth/users/nickname")
    public ResponseEntity<CommonResponse<String>> MakeRandomNickname(){
        return CommonResponse.success(joinService.makeRandomNickname());
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
            @ApiResponse(responseCode = "400-9", ref = "USER_NICKNAME_MISMATCH"),
            @ApiResponse(responseCode = "400-10", ref = "USER_NAME_MISMATCH"),
            @ApiResponse(responseCode = "404", ref = "USER_NOT_FOUND_AGREE"),
            @ApiResponse(responseCode = "409", ref = "USER_PHONE_NUMBER_DUPLICATION"),
    })
    @PostMapping("/auth/join")
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
            @ApiResponse(responseCode = "400-7", ref = "USER_NICKNAME_MISMATCH"),
            @ApiResponse(responseCode = "404", ref = "USER_NOT_FOUND_AGREE"),
            @ApiResponse(responseCode = "409", ref = "USER_PHONE_NUMBER_DUPLICATION"),
    })
    @PostMapping("/auth/join/kakao")
    public ResponseEntity<CommonResponse<String>> addKakaoUser(@RequestBody @Valid KakaoJoinReqDTO kakaoJoinReqDTO) throws CustomException {
        return joinService.addKakaoUser(kakaoJoinReqDTO);
    }
    /*000000000000000000000000000000 회원가입 관련 끝 000000000000000000000000000000*/
    /*############################## 로그인 관련 시작 ##############################*/
    @PostMapping("/auth/login")
    @Operation(summary = "로그인 요청",
            description = "로그인 성공 시 jwt 발급, 실패시 error <br> Refresh 토큰은 SameSite 문제로 설정되지 않고 있는 것 같습니다. AccessToken으로 확인하시길 바랍니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공"),
                    @ApiResponse(responseCode = "404", ref = "USER_NOT_FOUND"),
                    @ApiResponse(responseCode = "400", ref = "AUTH_METHOD_CONFLICT")
            })
    public ResponseEntity<CommonResponse<TokenResDTO>> login(@RequestBody @Valid LoginReqDTO loginReqDTO, HttpServletResponse response) {
        return CommonResponse.success(loginService.authenticateUser(loginReqDTO,response));
    }

    //리프레시 토큰
    @GetMapping("/auth/login/refresh")
    @Operation(summary = "새로운 JWT 토큰 발급 받기",
            description = "Refresh 토큰을 받아서 새로운 JWT 토큰 발급 받기",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "401", ref = "INVALID_TOKEN")
            })
    public ResponseEntity<CommonResponse<TokenResDTO>> refresh(@CookieValue("refreshToken") String refreshToken) throws CustomException {
        return CommonResponse.success(loginService.refreshJwtToken(refreshToken));
    }


    @Operation(summary = "카카오톡 로그인", description = "로그인 했는데 값이 없을 경우 isRequiredRedirect가 true 가 됩니다.<br>" +
            "userId,gender,phone값이 넘어갑니다. 랜덤 닉네임 생성 API 호출 해야합니다. <br> 로그인 시 선택 사항인 gender와 phone을 선택 안했으면 null로 넘어갑니다.<br>" +
            "성공시 isRequiredRedirect : false와 accessToken : 값, cookie에는 Refresh 토큰이 셋팅됩니다.<br>" +
            "카카오톡 로그인 URL : https://kauth.kakao.com/oauth/authorize?response_type=code&client_id={REST_API_KEY}&redirect_uri={redirectURL}<br>" +
            "REST_API_KEY : 카카오톡 developers 참고 <br>" +
            "redirectURL : http://localhost/v1/auth/login/kakao (테스트 전용 URL)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", ref = "AUTH_METHOD_CONFLICT")
    })
    @GetMapping("/auth/login/kakao")
    public ResponseEntity<CommonResponse<KakaoLoginResDTO>> loginKakao(@RequestParam("code")String code, HttpServletResponse response) {
        return CommonResponse.success(kakaoLoginService.loginKakao(code,response));
    }
    /*############################## 로그인 관련 끝 ##############################*/

    /*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ 인증 문자 관련 시작 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
    @Operation(summary = "인증 번호 문자 전송 통합" ,description = "<h3>type에 들어갈 수 있는 값 : '회원가입','아이디찾기'</h3>param : name <br> Body : type, phone")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상 접수(이통사로 접수 예정)"),
            @ApiResponse(responseCode = "400-1", ref = "USER_PHONE_NUMBER_FORMAT_MISMATCH"),
            @ApiResponse(responseCode = "400-2", ref = "USER_NAME_MISMATCH"),
            @ApiResponse(responseCode = "404", ref = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "409", ref = "USER_PHONE_NUMBER_DUPLICATION"),
    })
    @PostMapping("/auth/sms/verification-codes")
    public ResponseEntity<CommonResponse<String>> sendAuthenticationCode(@RequestBody @Valid FindByPhoneReqDTO phoneReqDTO,
                                                                         @RequestParam(value = "name",required = false) @Schema(description = "아이디 찾기 시 필수") @DynamicValid(errorCode = ErrorCode.USER_NAME_MISMATCH, pattern = Regex.NAME)
                                                                         String name){
        return switch (phoneReqDTO.getType()){
            case "아이디찾기" -> findService.sendAuthenticationCode(phoneReqDTO.getPhone(), name);
            case "회원가입" -> verificationService.sendVerificationMessage(phoneReqDTO);
            default -> throw new CustomRuntimeException(ErrorCode.BAD_PARAMETER);
        };
    }

    @Operation(summary = "인증 번호 문자 검증 통합",description = "<h3>type에 들어갈 수 있는 값 : '회원가입','아이디찾기'</h3>회원가입 Return : true/false <br> 아이디 찾기 Return : {<br>userId:string,<br>createdAt:string<br>} ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "true"),
            @ApiResponse(responseCode = "429", ref = "AUTH_TOO_MANY_REQUEST"),
            @ApiResponse(responseCode = "404", ref = "AUTH_NOT_FOUND_PHONE_CODE"),
            @ApiResponse(responseCode = "400-1", ref = "INVALID_AUTH_CODE"),
            @ApiResponse(responseCode = "400-2", ref = "USER_PHONE_NUMBER_FORMAT_MISMATCH"),
            @ApiResponse(responseCode = "400-3", ref = "AUTH_CODE_FORMAT_MISMATCH"),
            @ApiResponse(responseCode = "400-4", ref = "UNVERIFIED_AUTH_CODE"),
            @ApiResponse(responseCode = "400-5", ref = "MESSAGE_NOT_RESPONSE"),
            @ApiResponse(responseCode = "400-6", ref = "USER_NOT_FOUND_PHONE_NUMBER"),
    })
    @PostMapping("/auth/sms/validation")
    public ResponseEntity<CommonResponse<Object>> checkVerificationCode(@RequestBody @Valid CheckUserVerificationCodeReqDTO checkUserVerificationCodeReqDTO){
        return switch (checkUserVerificationCodeReqDTO.getType()){
            case "아이디찾기" -> CommonResponse.success(findService.checkVerificationCode(checkUserVerificationCodeReqDTO));
            case "회원가입" -> CommonResponse.success(verificationService.verifyCode(checkUserVerificationCodeReqDTO));
            default -> throw new CustomRuntimeException(ErrorCode.BAD_PARAMETER);
        };
    }

    /*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ 인증 문자 관련 끝 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
    /*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% 비밀번호 찾기 시작 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
    @Operation(summary = "비밀번호 찾기 1. 비밀번호 이메일 전송", description = "일치하는 아이디와 휴대폰 번호가 있는지 검증하고, 있으면 해당 이메일로 비밀번호 변경 주소 보냅니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400-1", ref = "USER_ID_NOT_EMAIL_FORMAT"),
            @ApiResponse(responseCode = "400-2", ref = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "400-3", ref = "USER_SUSPENDED"),
            @ApiResponse(responseCode = "500", ref = "MAIL_FAILED_RESPONSE"),
    })
    @PostMapping("/auth/password/{userId}/email")
    public ResponseEntity<CommonResponse<AuthFindResDTO>> chekcUserIdAndSendEmail(@RequestBody @Valid VerificationReqDTO phoneReqDTO,
                                                                                  @PathVariable
                                                                                  @DynamicValid(errorCode = ErrorCode.USER_ID_NOT_EMAIL_FORMAT,pattern = Regex.EMAIL)
                                                                                  String userId) throws MessagingException {
        return findService.sendEmailForPwd(userId, phoneReqDTO.getPhone());
    }

    @Operation(summary = "비밀번호 찾기 2. 비밀번호 변경", description = "10분 내로 인증하지 않으면 변경 불가, No와uuid를 param으로 받고 있습니다.<br> FE는 URL에서 얻을 수 있습니다.<br> Flutter는 테스트가 어렵습니다.<br> 이메일 전송까지가 완성으로 하겠습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400-1", ref = "USER_PASSWORD_PATTERN_MISMATCH"),
            @ApiResponse(responseCode = "400-2", ref = "USER_PASSWROD_FAILED_DOUBLE_CHECK"),
            @ApiResponse(responseCode = "400-3", ref = "AUTH_NOT_FOUND_EMAIL_USER"),
            @ApiResponse(responseCode = "404", ref = "USER_NOT_FOUND"),
    })
    @PutMapping("/auth/{no}/password/{uuid}")
    public ResponseEntity<CommonResponse<String>> changeUserPwd(@PathVariable Long no, @PathVariable String uuid,
                                                                @RequestBody @Valid CheckUserPwdReqDTO checkUserPwdReqDTO){
        return findService.changeUserPassword(no,uuid,checkUserPwdReqDTO);
    }
    /*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% 비밀번호 찾기 끝 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/

}
