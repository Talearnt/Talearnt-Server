package com.talearnt.login;

import com.talearnt.enums.ErrorCode;
import com.talearnt.util.exception.CustomException;
import com.talearnt.util.jwt.JwtTokenUtil;
import com.talearnt.util.jwt.TokenResDTO;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.version.RestControllerV1;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;



@RestControllerV1
@RequiredArgsConstructor
@Log4j2
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/auth/login")
    @Operation(summary = "로그인 요청",
            description = "로그인 성공 시 jwt 발급, 실패시 error",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공"),
                    @ApiResponse(responseCode = "404", ref = "USER_NOT_FOUND")
            })
    public ResponseEntity<CommonResponse<TokenResDTO>> login(@RequestBody LoginReqDTO loginReqDTO, HttpServletResponse response) {
        return CommonResponse.success(loginService.authenticateUser(loginReqDTO,response));
    }

    //리프레시 토큰
    @PostMapping("/auth/refresh")
    @Operation(summary = "새로운 JWT 토큰 발급 받기",
            description = "Refresh 토큰을 받아서 새로운 JWT 토큰 발급 받기",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "401", ref = "INVALID_TOKEN")
            })
    public ResponseEntity<CommonResponse<TokenResDTO>> refresh(@CookieValue("refreshToken") String refreshToken) throws CustomException {
        return CommonResponse.success(loginService.refreshJwtToken(refreshToken));
    }

    //카카오 로그인
    @PostMapping("/auth/login/kakao")
    @ResponseBody
    public ResponseEntity<CommonResponse<TokenResDTO>> loginKakao(@RequestBody LoginReqDTO loginReqDTO, HttpServletResponse response) {
        return CommonResponse.success(null);
    }

}
