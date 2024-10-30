package com.talearnt.login;

import com.talearnt.util.version.RestControllerV1;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/login")
    @ResponseBody
    public String login(@RequestBody LoginReqDTO loginReqDTO, HttpServletResponse response) {

        // 인증 객체 생성 및 인증 수행
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(loginReqDTO.getUserId(), loginReqDTO.getPw());
        Authentication auth = authenticationManagerBuilder.getObject().authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(auth);

        // JWT 생성
        String jwt = JwtTokenUtil.createToken(auth);
        System.out.println(jwt);

        // 쿠키 설정 및 추가
        Cookie cookie = new Cookie("jwt", jwt);
        cookie.setMaxAge(3600);  // 1시간 설정
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        response.addCookie(cookie);

        return jwt;
    }
    @PostMapping("/getUserId")
    @ResponseBody
    public String getUserId(String jwt) {

        // JWT에서 Claims 추출
        Claims claims = JwtTokenUtil.extractToken(jwt);

        // 각 정보 가져오기
        String userId = claims.get("userId", String.class);
//        String nickname = claims.get("nickname", String.class);
//        String authority = claims.get("authority", String.class);

        return userId;
    }


}
