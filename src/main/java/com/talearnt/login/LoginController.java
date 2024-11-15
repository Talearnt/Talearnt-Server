package com.talearnt.login;

import com.talearnt.login.company.LoginReqDTO;
import com.talearnt.login.kakao.KakaoLoginResDTO;
import com.talearnt.util.exception.CustomException;
import com.talearnt.util.jwt.TokenResDTO;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.version.RestControllerV1;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "2. 로그인",description = "카카오톡 로그인 아직 미구현")
@RestControllerV1
@RequiredArgsConstructor
@Log4j2
public class LoginController {

    private final LoginService loginService;
    private final KakaoLoginService kakaoLoginService;

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
    @GetMapping("/auth/refresh")
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
            "userId,gender,phone값이 넘어갑니다.<br> 로그인 시 선택 사항인 gender와 phone을 선택 안했으면 null로 넘어갑니다.<br>" +
            "성공시 isRequiredRedirect : false와 accessToken : 값, cookie에는 Refresh 토큰이 셋팅됩니다.<br>" +
            "카카오톡 로그인 URL : https://kauth.kakao.com/oauth/authorize?response_type=code&client_id={REST_API_KEY}&redirect_uri={redirectURL}<br>" +
            "REST_API_KEY : 카카오톡 developers 참고 <br>" +
            "redirectURL : http://localhost/v1/api/auth/login/kakao (테스트 전용 URL)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", ref = "AUTH_METHOD_CONFLICT")
    })
    @GetMapping("/auth/login/kakao")
    public ResponseEntity<CommonResponse<KakaoLoginResDTO>> loginKakao(@RequestParam("code")String code, HttpServletResponse response) {
        return CommonResponse.success(kakaoLoginService.loginKakao(code,response));
    }

}
