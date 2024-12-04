package com.talearnt.auth;

import com.talearnt.auth.find.reponse.AuthFindResDTO;
import com.talearnt.auth.find.request.CheckUserPwdReqDTO;
import com.talearnt.auth.find.request.CheckUserVerificationCodeReqDTO;
import com.talearnt.auth.find.request.FindByPhoneReqDTO;
import com.talearnt.auth.join.request.JoinReqDTO;
import com.talearnt.auth.join.request.KakaoJoinReqDTO;
import com.talearnt.auth.login.company.LoginReqDTO;
import com.talearnt.auth.login.kakao.KakaoLoginResDTO;
import com.talearnt.auth.verification.VerificationReqDTO;
import com.talearnt.enums.common.ErrorCode;
import com.talearnt.enums.common.Regex;
import com.talearnt.util.exception.CustomException;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.jwt.TokenResDTO;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.valid.DynamicValid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface AuthApi {
    /*000000000000000000000000000000 회원가입 관련 시작 000000000000000000000000000000*/
    @Operation(summary = "아이디 중복 확인 - 중복O True, 중복X False")
    @ApiResponse(responseCode = "400", ref = "USER_ID_NOT_EMAIL_FORMAT")
    public ResponseEntity<CommonResponse<Boolean>> checkDuplicatedUserID(@PathVariable @DynamicValid(errorCode = ErrorCode.USER_ID_NOT_EMAIL_FORMAT, pattern = Regex.EMAIL)
                                                                         String userId);

    @Operation(summary = "닉네임 중복 확인 - 중복O True, 중복X False")
    public ResponseEntity<CommonResponse<Boolean>> checkDuplicatedNickname(@PathVariable @DynamicValid(errorCode = ErrorCode.DUPLICATE_USER_NICKNAME,pattern = Regex.NICKNAME)
                                                                           String nickname);

    @Operation(summary = "랜덤한 닉네임 생성, 회원가입 닉네임 입력창으로 이동 시 호출")
    public ResponseEntity<CommonResponse<String>> MakeRandomNickname();

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
    public ResponseEntity<CommonResponse<String>> addUser(@RequestBody @Valid JoinReqDTO joinReqDTO) throws CustomException;


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
    public ResponseEntity<CommonResponse<String>> addKakaoUser(@RequestBody @Valid KakaoJoinReqDTO kakaoJoinReqDTO) throws CustomException;
    /*000000000000000000000000000000 회원가입 관련 끝 000000000000000000000000000000*/
    /*############################## 로그인 관련 시작 ##############################*/
    @Operation(summary = "로그인 요청",
            description = "로그인 성공 시 jwt 발급, 실패시 error <br> Refresh 토큰은 SameSite 문제로 설정되지 않고 있는 것 같습니다. AccessToken으로 확인하시길 바랍니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공"),
                    @ApiResponse(responseCode = "404", ref = "USER_NOT_FOUND"),
                    @ApiResponse(responseCode = "400", ref = "AUTH_METHOD_CONFLICT")
            })
    public ResponseEntity<CommonResponse<TokenResDTO>> login(@RequestBody @Valid LoginReqDTO loginReqDTO, HttpServletResponse response);

    //리프레시 토큰
    @Operation(summary = "새로운 JWT 토큰 발급 받기",
            description = "Refresh 토큰을 받아서 새로운 JWT 토큰 발급 받기",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "401", ref = "INVALID_TOKEN")
            })
    public ResponseEntity<CommonResponse<TokenResDTO>> refresh(@CookieValue("refreshToken") String refreshToken) throws CustomException;

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
    public ResponseEntity<CommonResponse<KakaoLoginResDTO>> loginKakao(@RequestParam("code")String code, HttpServletResponse response);

    /*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ 인증 문자 관련 시작 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
    @Operation(summary = "인증 번호 문자 전송 통합" ,
            description = "<h3>Body 설명</h3>" +
                    "<ul>" +
                        "<li><strong>type : </strong> 아이디 찾기 - findId, 회원가입 - signUp</li>" +
                        "<li><strong>phone : </strong> 휴대폰 번호 11자리</li>" +
                        "<li><strong>name : </strong> 회원가입 - `null`</li>" +
                    "</ul>" +
                    "<hr />" +
                    "<p>회원가입의 경우 name 값이 없어도 됩니다.</p>" +
                    "<p>문자 인증 1분 안에 5회 이상 요청할 경우 10분간 요청을 막습니다.(구현중)</p>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상 접수(이통사로 접수 예정)"),
            @ApiResponse(responseCode = "400-1", ref = "USER_PHONE_NUMBER_FORMAT_MISMATCH"),
            @ApiResponse(responseCode = "400-2", ref = "USER_NAME_MISMATCH"),
            @ApiResponse(responseCode = "404", ref = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "409", ref = "USER_PHONE_NUMBER_DUPLICATION"),
    })
    public ResponseEntity<CommonResponse<String>> sendAuthenticationCode(@RequestBody @Valid FindByPhoneReqDTO phoneReqDTO, HttpServletRequest request);

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
    public ResponseEntity<CommonResponse<Object>> checkVerificationCode(@RequestBody @Valid CheckUserVerificationCodeReqDTO checkUserVerificationCodeReqDTO);

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
    public ResponseEntity<CommonResponse<AuthFindResDTO>> chekcUserIdAndSendEmail(@RequestBody @Valid VerificationReqDTO phoneReqDTO,
                                                                                  @PathVariable
                                                                                  @DynamicValid(errorCode = ErrorCode.USER_ID_NOT_EMAIL_FORMAT,pattern = Regex.EMAIL)
                                                                                  String userId) throws MessagingException;

    @Operation(summary = "비밀번호 찾기 2. 비밀번호 변경", description = "10분 내로 인증하지 않으면 변경 불가, No와uuid를 param으로 받고 있습니다.<br> FE는 URL에서 얻을 수 있습니다.<br> Flutter는 테스트가 어렵습니다.<br> 이메일 전송까지가 완성으로 하겠습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400-1", ref = "USER_PASSWORD_PATTERN_MISMATCH"),
            @ApiResponse(responseCode = "400-2", ref = "USER_PASSWROD_FAILED_DOUBLE_CHECK"),
            @ApiResponse(responseCode = "400-3", ref = "AUTH_NOT_FOUND_EMAIL_USER"),
            @ApiResponse(responseCode = "404", ref = "USER_NOT_FOUND"),
    })
    public ResponseEntity<CommonResponse<String>> changeUserPwd(@PathVariable Long no, @PathVariable String uuid,
                                                                @RequestBody @Valid CheckUserPwdReqDTO checkUserPwdReqDTO);
    /*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% 비밀번호 찾기 끝 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/

}
