package com.talearnt.auth;

import com.talearnt.auth.join.JoinService;
import com.talearnt.enums.common.ErrorCode;
import com.talearnt.enums.common.Regex;
import com.talearnt.auth.join.request.JoinReqDTO;
import com.talearnt.auth.join.request.KakaoJoinReqDTO;
import com.talearnt.auth.login.KakaoLoginService;
import com.talearnt.auth.login.LoginService;
import com.talearnt.auth.login.company.LoginReqDTO;
import com.talearnt.auth.login.kakao.KakaoAccessTokenReqDTO;
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

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth",description = "회원가입/로그인/인증 문자 전송/아이디,비밀번호 찾기")
@RestControllerV1
@RequiredArgsConstructor
@Log4j2
@Validated
public class AuthController implements AuthApi{

    private final LoginService loginService;
    private final KakaoLoginService kakaoLoginService;

    //회원 가입
    private final JoinService joinService;
    //회원 가입 전용 인증 문자
    private final VerificationService verificationService;
    //아이디 찾기 전용 인증 문자
    private final AuthFindService findService;

    /*000000000000000000000000000000 회원가입 관련 시작 000000000000000000000000000000*/
    @GetMapping("/auth/users/id")
    public ResponseEntity<CommonResponse<Boolean>> checkDuplicatedUserID(@RequestParam @DynamicValid(errorCode = ErrorCode.USER_ID_NOT_EMAIL_FORMAT, pattern = Regex.EMAIL)
                                                                         String userId){
        return joinService.checkDuplicatedUserId(userId);
    }

    @GetMapping("/auth/users/nickname/availability")
    public ResponseEntity<CommonResponse<Boolean>> checkDuplicatedNickname(@RequestParam @DynamicValid(errorCode = ErrorCode.USER_NICKNAME_MISMATCH,pattern = Regex.NICKNAME)
                                                                           String nickname){
        return CommonResponse.success(joinService.checkDuplicatedNickname(nickname));
    }


    @GetMapping("/auth/users/nickname/random")
    public ResponseEntity<CommonResponse<String>> MakeRandomNickname(){
        return CommonResponse.success(joinService.makeRandomNickname());
    }


    @PostMapping("/auth/join")
    public ResponseEntity<CommonResponse<String>> addUser(@RequestBody @Valid JoinReqDTO joinReqDTO) throws CustomException {
        return joinService.registerUser(joinReqDTO);
    }



    @PostMapping("/auth/join/kakao")
    public ResponseEntity<CommonResponse<String>> addKakaoUser(@RequestBody @Valid KakaoJoinReqDTO kakaoJoinReqDTO) throws CustomException {
        return joinService.addKakaoUser(kakaoJoinReqDTO);
    }
    /*000000000000000000000000000000 회원가입 관련 끝 000000000000000000000000000000*/
    /*############################## 로그인 관련 시작 ##############################*/
    @PostMapping("/auth/login")
    public ResponseEntity<CommonResponse<TokenResDTO>> login(@RequestBody @Valid LoginReqDTO loginReqDTO, HttpServletResponse response) {
        return CommonResponse.success(loginService.authenticateUser(loginReqDTO,response));
    }

    //리프레시 토큰
    @GetMapping("/auth/login/refresh")
    public ResponseEntity<CommonResponse<TokenResDTO>> refresh(@CookieValue("refreshToken") String refreshToken) throws CustomException {
        return CommonResponse.success(loginService.refreshJwtToken(refreshToken));
    }


    //웹 방식 카카오 로그인
    @GetMapping("/auth/login/kakao")
    public ResponseEntity<CommonResponse<KakaoLoginResDTO>> loginKakao(@RequestParam("code")String code, HttpServletResponse response) {
        return CommonResponse.success(kakaoLoginService.loginKakao(code,response));
    }

    //모바일 방식 카카오 로그인
    @PostMapping("/auth/login/kakao/mobile")
    public ResponseEntity<CommonResponse<KakaoLoginResDTO>> loginKakaoForMobile(@RequestBody KakaoAccessTokenReqDTO accessTokenReqDTO, HttpServletResponse response){
        return CommonResponse.success(kakaoLoginService.loginKakaoForMobile(accessTokenReqDTO.getKakaoAccessToken(), accessTokenReqDTO.isAutoLogin(), response));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<CommonResponse<Void>> logout(HttpServletResponse response) {
        loginService.logout(response);
        return CommonResponse.success(null);
    }

    /*############################## 로그인 관련 끝 ##############################*/

    /*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ 인증 문자 관련 시작 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/

    @PostMapping("/auth/sms/verification-codes")
    public ResponseEntity<CommonResponse<String>> sendAuthenticationCode(@RequestBody @Valid FindByPhoneReqDTO phoneReqDTO,
                                                                         HttpServletRequest request){
        // SMS 요청 1분 이내 6번 이상 요청했는 지 검증
        if (!verificationService.isAllowedIp(request.getRemoteAddr())){
            log.error("SMS Controller - SMS 요청이 너무 많음 : {} ",request.getRemoteAddr());
            throw new CustomRuntimeException(ErrorCode.AUTH_SMS_TOO_MANY_REQUEST);
        }

        return switch (phoneReqDTO.getType()){
            case "findId" -> findService.sendAuthenticationCode(phoneReqDTO.getPhone(), phoneReqDTO.getName());
            case "signUp" -> verificationService.sendVerificationMessage(phoneReqDTO);
            default -> throw new CustomRuntimeException(ErrorCode.BAD_PARAMETER);
        };
    }

    @PostMapping("/auth/sms/validation")
    public ResponseEntity<CommonResponse<Object>> checkVerificationCode(@RequestBody @Valid CheckUserVerificationCodeReqDTO checkUserVerificationCodeReqDTO){
        return switch (checkUserVerificationCodeReqDTO.getType()){
            case "findId" -> CommonResponse.success(findService.checkVerificationCode(checkUserVerificationCodeReqDTO));
            case "signUp" -> CommonResponse.success(verificationService.verifyCode(checkUserVerificationCodeReqDTO));
            default -> throw new CustomRuntimeException(ErrorCode.BAD_PARAMETER);
        };
    }

    /*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ 인증 문자 관련 끝 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
    /*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% 비밀번호 찾기 시작 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/

    @PostMapping("/auth/password/email")
    public ResponseEntity<CommonResponse<AuthFindResDTO>> chekcUserIdAndSendEmail(@RequestBody @Valid VerificationReqDTO phoneReqDTO) throws MessagingException {
        return findService.sendEmailForPwd(phoneReqDTO.getUserId(), phoneReqDTO.getPhone());
    }

    @PutMapping("/auth/{no}/password/{uuid}")
    public ResponseEntity<CommonResponse<String>> changeUserPwd(@PathVariable Long no, @PathVariable String uuid,
                                                                @RequestBody @Valid CheckUserPwdReqDTO checkUserPwdReqDTO){
        return findService.changeUserPassword(no,uuid,checkUserPwdReqDTO);
    }
    /*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% 비밀번호 찾기 끝 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/

    @DeleteMapping("/test/auth/{userId}")
    public ResponseEntity<CommonResponse<String>> deleteTestUserId(@PathVariable String userId){
        return CommonResponse.success(joinService.deleteTestUserId(userId));
    }

}
