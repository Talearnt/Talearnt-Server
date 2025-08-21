package com.talearnt.auth;

import com.talearnt.auth.find.reponse.AuthFindResDTO;
import com.talearnt.auth.find.request.CheckUserPwdReqDTO;
import com.talearnt.auth.find.request.CheckUserVerificationCodeReqDTO;
import com.talearnt.auth.find.request.FindByPhoneReqDTO;
import com.talearnt.auth.join.request.JoinReqDTO;
import com.talearnt.auth.join.request.KakaoJoinReqDTO;
import com.talearnt.auth.login.company.LoginReqDTO;
import com.talearnt.auth.login.kakao.KakaoAccessTokenReqDTO;
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
    @Operation(summary = "아이디 중복 확인",
    description = "<p>아이디 중복을 체크하는 API입니다.</p>" +
            "<ul>" +
                "<li>true : 중복임</li>" +
                "<li>false : 중복 아님</li>" +
            "</ul>")
    @ApiResponse(responseCode = "400", ref = "USER_ID_NOT_EMAIL_FORMAT")
    public ResponseEntity<CommonResponse<Boolean>> checkDuplicatedUserID(@RequestParam @DynamicValid(errorCode = ErrorCode.USER_ID_NOT_EMAIL_FORMAT, pattern = Regex.EMAIL)
                                                                         String userId);


    @Operation(summary = "닉네임 중복 확인",
            description = "<p><strong>닉네임 중복 확인 입니다.</strong></p>" +
                    "<p>닉네임 중복 확인은 <code>query parameter</code> 로 <code>닉네임(필수)</code>을 보내주시면 됩니다.</p>" +
                    "<hr>" +
                    "<h2>닉네임 중복 내용</h2>" +
                    "<p>닉네임 중복을 체크하는 API입니다.</p>" +
                    "<h2>Response</h2>" +
                    "<ul>" +
                    "<li>true : 중복임</li>" +
                    "<li>false : 중복 아님</li>" +
                    "</ul>")
    @ApiResponse(responseCode = "400", ref = "USER_NICKNAME_MISMATCH")
    public ResponseEntity<CommonResponse<Boolean>> checkDuplicatedNickname(@RequestParam @DynamicValid(errorCode = ErrorCode.USER_NICKNAME_MISMATCH,pattern = Regex.NICKNAME)
                                                                           String nickname);


    @Operation(summary = "랜덤 닉네임 생성",
            description = "<p><strong>랜덤 닉네임 생성입니다.</strong></p>" +
                    "<hr>" +
                    "<h2>랜덤 닉네임 생성 내용</h2>" +
                    "<p>랜덤한 닉네임을 생성합니다. 중복 검사를 거친 닉네임입니다.</p>" +
                    "<h2>Response</h2>" +
                    "<p>ex) <strong>유쾌한범고래#46</strong></p>" +
                    "<p>형용사(3~4글자) + 명사(2~3글자) + # + 1~100</p>" +
                    "<p>최대 11자로 생성됩니다.</p>" +
                    "<p>기획서에 입력된 내용처럼 닉네임은 최소 2자~12자로 보내주셔야 합니다.</p>")
    public ResponseEntity<CommonResponse<String>> MakeRandomNickname();



    @Operation(summary = "휴대폰 인증까지 마치고 회원 가입 완료 단계, 자사 회원 가입",
            description = "<h2>내용</h2>\n" +
                    "<p>휴대폰 번호 인증이 완료된 후에 가입이 가능합니다.</p>\n" +
                    "<hr>\n" +
                    "<p>필수 이용 약관도 true 로 반드시 보내주셔야 합니다.</p>" +
                    "<p>이용 약관 코드 ID는 <code>관리자 페이지 : 관리자 전용 - 활성화된 이용 약관</code> 에서 활성화된 이용 약관 내용을 참고하여 주시길 바랍니다.</p>" +
                    "<p><code>관리자 페이지 : 관리자 전용 - 활성화된 이용 약관</code> 은 따로 호출 하지 않고, 하드 코딩으로 작업해주시길 바랍니다.</p>" +
                    "<p>이용 약관 제목, 버전, 내용, 등록 일시는 문제 발생 시 증빙서류로 가지고 있어야 한다고 합니다.</p>" +
                    "<p><strong>DB 에는 존재하지만, 회원가입 시 호출하여 등록하지 않고 하드코딩으로 작업해주시길 바랍니다.</strong></p>")
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
            @ApiResponse(responseCode = "400-11", ref = "TERMS_INVALID_VERSION"),
            @ApiResponse(responseCode = "404", ref = "USER_NOT_FOUND_AGREE"),
            @ApiResponse(responseCode = "409", ref = "USER_PHONE_NUMBER_DUPLICATION"),
    })
    public ResponseEntity<CommonResponse<String>> addUser(@RequestBody @Valid JoinReqDTO joinReqDTO) throws CustomException;


    @Operation(summary = "카카오톡 회원가입",
            description = "<h2>내용</h2>\n" +
                    "<p>필수 이용 약관도 true 로 반드시 보내주셔야 합니다.</p>" +
                    "<p>이용 약관 코드 ID는 <code>관리자 페이지 : 관리자 전용 - 활성화된 이용 약관</code> 에서 활성화된 이용 약관 내용을 참고하여 주시길 바랍니다.</p>" +
                    "<p><code>관리자 페이지 : 관리자 전용  활성화된 이용 약관</code> 은 따로 호출 하지 않고, 하드 코딩으로 작업합니다.</p>" +
                    "<p>이용 약관 제목, 버전, 내용은 문제 발생 시 증빙서류로 가지고 있어야 한다고 합니다.</p>" +
                    "<p><strong>DB 에는 존재하지만, 회원가입 시 호출하여 등록하지 않고 하드코딩으로 작업해주시길 바랍니다.</strong></p>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카카오톡 회원가입 성공"),
            @ApiResponse(responseCode = "400-1", ref = "UNVERIFIED_AUTH_CODE"),
            @ApiResponse(responseCode = "400-2", ref = "USER_ID_NOT_EMAIL_FORMAT"),
            @ApiResponse(responseCode = "400-3", ref = "USER_GENDER_MISSMATCH"),
            @ApiResponse(responseCode = "400-4", ref = "USER_PHONE_NUMBER_FORMAT_MISMATCH"),
            @ApiResponse(responseCode = "400-5", ref = "DUPLICATE_USER_ID"),
            @ApiResponse(responseCode = "400-6", ref = "USER_REQUIRED_NOT_AGREE"),
            @ApiResponse(responseCode = "400-7", ref = "USER_NICKNAME_MISMATCH"),
            @ApiResponse(responseCode = "400-8", ref = "TERMS_INVALID_VERSION"),
            @ApiResponse(responseCode = "404", ref = "USER_NOT_FOUND_AGREE"),
            @ApiResponse(responseCode = "409", ref = "USER_PHONE_NUMBER_DUPLICATION"),
    })
    public ResponseEntity<CommonResponse<String>> addKakaoUser(@RequestBody @Valid KakaoJoinReqDTO kakaoJoinReqDTO) throws CustomException;
    /*000000000000000000000000000000 회원가입 관련 끝 000000000000000000000000000000*/
    /*############################## 로그인 관련 시작 ##############################*/
    @Operation(summary = "로그인 요청",
            description = "<h2>내용</h2>\n" +
                    "<p>로그인 성공 시 JWT 토큰 발급</p>\n" +
                    "<p>Refresh 토큰은 SameSite=None으로 설정되었습니다.</p>" +
                    "<p>모바일 여러분은 SSL 인증서가 필요하면 카톡으로 요청 부탁드리겠습니다.</p>"+
                    "<p>모바일 적용에 대한 이해도가 낮으므로 필요 시 문서도 같이 보내주시면 감사하겠습니다!</p>" +
                    "<h2>개선 필요</h2>" +
                    "<p>SameSite=Strict로 변경</p>" +
                    "<p>서브 도메인일 경우 같은 도메인으로 인식하지 않은 문제이므로, cookie setDomain 테스트, 같은 도메인으로 변경 등 다양한 시도가 필요</p>",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공"),
                    @ApiResponse(responseCode = "404", ref = "USER_NOT_FOUND"),
                    @ApiResponse(responseCode = "403-1", ref = "USER_SUSPENDED"),
                    @ApiResponse(responseCode = "403-2", ref = "USER_WITH_DRAWN"),
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

    @Operation(summary = "카카오톡 로그인", description = "<p>카카오톡 로그인시 첫 로그인일 경우 회원가입 페이지로 넘어갑니다.</p>" +
            "<p>닉네임은 랜덤 닉네임 생성 API를 호출해주셔야 합니다.</p>" +
            "<hr/>" +
            "<p>카카오톡으로 로그인이 성공했을 경우에는 성공적으로 정상적으로 AccessToken이 응답 값으로 넘어갑니다.</p>" +
            "<p>Refresh Token은 Cookie에 셋팅됩니다.</p>" +
            "<hr/>" +
            "<h2>회원 가입으로 넘어가기 전 분기점</h2>" +
            "<p>카카오 로그인 시도 시, 비회원이고 가입한 휴대폰 번호가 존재할 경우에 409 Exception이 발생합니다.</p>" +
            "<p>비회원이고 휴대폰 번호 중복도 없으면 카카오에서 제공한 값이 반환됩니다.</p>" +
            "<hr/>" +
            "<h2>로그인 성공 시 Response</h2>" +
            "<ul>" +
                "<li>isRegistered : true</li>" +
                "<li>accessToken : 자사 어세스 토큰 반환</li>" +
                "<li>userId : null</li>" +
                "<li>name : null</li>" +
                "<li>phone : null</li>" +
                "<li>gender : null</li>" +
            "</ul>" +
            "<hr/>" +
            "<h2>로그인 실패시 시 Response</h2>" +
            "<ul>" +
            "<li>isRegistered : false</li>" +
            "<li>accessToken : null</li>" +
            "<li>userId : 카카오 이메일</li>" +
            "<li>name : 카카오 이름</li>" +
            "<li>phone : 카카오 휴대폰 번호</li>" +
            "<li>gender : 카카오 성별</li>" +
            "</ul>" +
            "<hr/>" +
            "<p><strong>카카오톡 로그인 URL :</strong> https://kauth.kakao.com/oauth/authorize?response_type=code&client_id={REST_API_KEY}&redirect_uri={redirectURL} <p>" +
            "<p><strong>REST_API_KEY :</strong> 카카오톡 developers 참고<p>" +
            "<p><strong>redirectURI :</strong> 카카오에 RedirectURI 등록 필요, Code 받아 Server 로 전송 -> /v1/auth/login/kakao<p>" +
            "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", ref = "AUTH_METHOD_CONFLICT"),
            @ApiResponse(responseCode = "403-1", ref = "USER_SUSPENDED"),
            @ApiResponse(responseCode = "403-2", ref = "USER_WITH_DRAWN"),
            @ApiResponse(responseCode = "409", ref = "USER_PHONE_NUMBER_DUPLICATION"),
    })
    public ResponseEntity<CommonResponse<KakaoLoginResDTO>> loginKakao(@RequestParam("code")String code, HttpServletResponse response);


    @Operation(summary = "Flutter용 모바일 카카오톡 로그인", description = "<p>Flutter 클라이언트에서 카카오 SDK를 통해 직접 받은 Access Token을 사용하여 로그인하는 API입니다.</p>" +
            "<p>웹 방식과 달리 Redirection 없이 Access Token을 직접 전달받아 처리합니다.</p>" +
            "<hr/>" +
            "<h2>Flutter 클라이언트에서의 사용법</h2>" +
            "<ol>" +
                "<li>Flutter에서 카카오 SDK를 사용하여 Access Token을 발급받습니다</li>" +
                "<li>해당 Access Token을 서버의 /auth/login/kakao/mobile 엔드포인트로 전송합니다</li>" +
                "<li>서버는 카카오 API를 호출하여 사용자 정보를 조회하고 로그인 처리를 합니다</li>" +
                "<li>결과에 따라 JWT 토큰을 받거나 회원가입 정보를 받습니다</li>" +
            "</ol>" +
            "<hr/>" +
            "<h2>Request Body</h2>" +
            "<ul>" +
                "<li><strong>kakaoAccessToken : </strong>Flutter에서 받은 카카오 Access Token (필수)</li>" +
                "<li><strong>autoLogin : </strong>자동 로그인 여부 (true/false)</li>" +
            "</ul>" +
            "<hr/>" +
            "<h2>회원 가입으로 넘어가기 전 분기점</h2>" +
            "<p>카카오 로그인 시도 시, 비회원이고 가입한 휴대폰 번호가 존재할 경우에 409 Exception이 발생합니다.</p>" +
            "<p>비회원이고 휴대폰 번호 중복도 없으면 카카오에서 제공한 값이 반환됩니다.</p>" +
            "<hr/>" +
            "<h2>로그인 성공 시 Response</h2>" +
            "<ul>" +
                "<li>isRegistered : true</li>" +
                "<li>accessToken : 자사 JWT Access Token</li>" +
                "<li>userId : null</li>" +
                "<li>name : null</li>" +
                "<li>phone : null</li>" +
                "<li>gender : null</li>" +
            "</ul>" +
            "<hr/>" +
            "<h2>로그인 실패시 Response (회원가입 필요)</h2>" +
            "<ul>" +
                "<li>isRegistered : false</li>" +
                "<li>accessToken : null</li>" +
                "<li>userId : 카카오 이메일</li>" +
                "<li>name : 카카오 이름</li>" +
                "<li>phone : 카카오 휴대폰 번호</li>" +
                "<li>gender : 카카오 성별</li>" +
            "</ul>" +
            "<hr/>" +
            "<h2>웹 방식과의 차이점</h2>" +
            "<ul>" +
                "<li><strong>웹 방식 : </strong>카카오에서 인가 코드(code)를 받아 서버에서 Access Token을 발급받음</li>" +
                "<li><strong>모바일 방식 : </strong>Flutter에서 직접 Access Token을 받아 서버로 전송</li>" +
                "<li><strong>공통점 : </strong>동일한 사용자 정보 조회 및 로그인 처리 로직 사용</li>" +
            "</ul>" +
            "<hr/>" +
            "<p><strong>엔드포인트 :</strong> POST /v1/auth/login/kakao/mobile</p>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공 또는 회원가입 정보 반환"),
            @ApiResponse(responseCode = "400", ref = "AUTH_METHOD_CONFLICT"),
            @ApiResponse(responseCode = "403-1", ref = "USER_SUSPENDED"),
            @ApiResponse(responseCode = "403-2", ref = "USER_WITH_DRAWN"),
            @ApiResponse(responseCode = "409", ref = "USER_PHONE_NUMBER_DUPLICATION"),
    })
    public ResponseEntity<CommonResponse<KakaoLoginResDTO>> loginKakaoForMobile(@RequestBody KakaoAccessTokenReqDTO accessTokenReqDTO, HttpServletResponse response);


    @Operation(
            summary = "로그아웃",
            description = "<h3>내용</h3>" +
                    "<p>클라이언트의 리프레시 토큰 쿠키를 만료시켜 로그아웃 처리합니다.</p>" +
                    "<p>어세스 토큰은 클라이언트에서 삭제를 시켜줘야 정상적으로 로그아웃됩니다.</p>"
    )
    @ApiResponse(
            responseCode = "200",
            description = "로그아웃 성공"
    )
    @PostMapping("/auth/logout")
    public ResponseEntity<CommonResponse<Void>> logout(HttpServletResponse response);


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
                    "<p>문자 인증 1분 안에 5회 이상 요청할 경우 10분간 요청을 막습니다.</p>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상 접수(이통사로 접수 예정)"),
            @ApiResponse(responseCode = "400-1", ref = "USER_PHONE_NUMBER_FORMAT_MISMATCH"),
            @ApiResponse(responseCode = "400-2", ref = "USER_NAME_MISMATCH"),
            @ApiResponse(responseCode = "404", ref = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "409", ref = "USER_PHONE_NUMBER_DUPLICATION"),
    })
    public ResponseEntity<CommonResponse<String>> sendAuthenticationCode(@RequestBody @Valid FindByPhoneReqDTO phoneReqDTO, HttpServletRequest request);

    @Operation(summary = "인증 번호 문자 검증 통합",
            description = "<h2>인증 번호 검증 Type에 따라서 나오는 결과물이 달라집니다.</h2>" +
                    "<p>`signUp` (회원가입)</p>" +
                    "<pre>" +
                    "{<br>" +
                    "   success: true,<br>" +
                    "   data : true,<br>" +
                    "   errorCode : null,<br>" +
                    "   errorMessage : null<br>" +
                    "}<br>" +
                    "</pre>" +
                    "<p>`findId` (아이디 찾기)</p>" +
                    "<pre>" +
                    "{<br>" +
                    "   success: true,<br>" +
                    "   data : {<br>" +
                    "               userId : string,<br>" +
                    "               createdAt : string<br>" +
                    "           }<br>" +
                    "   errorCode : null,<br>" +
                    "   errorMessage : null<br>" +
                    "}<br>" +
                    "</pre>")
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
    @Operation(summary = "비밀번호 찾기 1. 비밀번호 이메일 전송",
            description = "<h2>설명</h2>" +
                    "<p>아이디와 휴대폰 번호가 일치하는 회원이 있을 경우 이메일이 전송됩니다.</p>" +
                    "<hr/>" +
                    "<h3>수정할 내용</h3>" +
                    "<p>현재 로고 이미지와 고객센터 주소가 입력되어 있지 않습니다.</p>" +
                    "<p>로고가 깨지고, 고객센터가 작동하지 않는 것이 정상입니다.</p>" +
                    "<p>추후 URL 등록과 로고 업로드 후 적용시키겠습니다.</p>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400-1", ref = "USER_ID_NOT_EMAIL_FORMAT"),
            @ApiResponse(responseCode = "400-2", ref = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "400-3", ref = "USER_SUSPENDED"),
            @ApiResponse(responseCode = "500", ref = "MAIL_FAILED_RESPONSE"),
    })
    public ResponseEntity<CommonResponse<AuthFindResDTO>> chekcUserIdAndSendEmail(@RequestBody @Valid VerificationReqDTO phoneReqDTO) throws MessagingException;

    @Operation(summary = "비밀번호 찾기 2. 비밀번호 변경",
            description = "<h2>내용</h2>\n" +
                    "<p>10분 이내로 비밀번호를 변경하지 않을 경우 변경이 불가합니다.</p>\n" +
                    "<p><code>no</code>와<code>uuid</code>를 Path로 보내주시면 됩니다.</p>\n" +
                    "<p><strong>a 태그에 등록된 URL :</strong> http://localhost:5173/{no}/password/{uuid}</p>" +
                    "<p>URL에 대해 경로 변경을 원하시면 말씀주세요.</p>" +
                    "<p>localhost는 도메인 등록하면 변경할 내용입니다.</p>" +
                    "<h3>Body</h3>\n" +
                    "<ul>\n" +
                    "    <li>pw: 변경할 비밀번호</li>\n" +
                    "    <li>checkedPw: 변경할 비밀번호 확인</li>\n" +
                    "</ul>\n" +
                    "<hr/>\n" +
                    "<p>Flutter는 이메일에서 변경하는 것이 어려울 것으로 판단됩니다.</p>\n" +
                    "<p>모바일은 이메일 전송까지가 완성으로 하겠습니다.</p>")
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
