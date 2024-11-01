package com.talearnt.login;

import com.talearnt.enums.ErrorCode;
import com.talearnt.util.jwt.JwtTokenUtil;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.version.RestControllerV1;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;



@RestControllerV1
@RequiredArgsConstructor
public class LoginController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final LoginService loginService;

    @PostMapping("/login")
    @ResponseBody
    @Operation(summary = "로그인 요청",
            description = "로그인 성공 시 jwt 발급, 실패시 error",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공"),
                    @ApiResponse(responseCode = "401", ref = "INVALID_CREDENTIALS")
            })
    public ResponseEntity<CommonResponse<String>> login(@RequestBody LoginReqDTO loginReqDTO, HttpServletResponse response) {
        try {
            // LoginService에서 인증 수행 후 Authentication 반환
            Authentication auth = loginService.authenticateUser(loginReqDTO);
            SecurityContextHolder.getContext().setAuthentication(auth);

            // JWT 생성
            String jwt = JwtTokenUtil.createToken(auth);
            Cookie cookie = new Cookie("jwt", jwt);
            cookie.setMaxAge(3600);  // 1시간 설정
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);

            return CommonResponse.success("로그인 성공"); // JWT 반환
        } catch (BadCredentialsException e) {
            // 인증 실패 시 오류 메시지 및 상태 코드 반환
            return CommonResponse.error(ErrorCode.INVALID_CREDENTIALS);
        }
    }


    //카카오 로그인
    @PostMapping("/loginKakao")
    @ResponseBody
    public String loginKakao(@RequestBody LoginReqDTO loginReqDTO, HttpServletResponse response) {

        // 인증 객체 생성 및 인증 수행
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(loginReqDTO.getUserId(), loginReqDTO.getPw());
        Authentication auth = authenticationManagerBuilder.getObject().authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(auth);

        // JWT 생성
        String jwt = JwtTokenUtil.createToken(auth);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwt);

        // 쿠키 설정 및 추가
        Cookie cookie = new Cookie("jwt", jwt);
        cookie.setMaxAge(3600);  // 1시간 설정
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        response.addCookie(cookie);

        return jwt;
    }

}
