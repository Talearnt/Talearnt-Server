package com.talearnt.util.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();
        String jwtCookie = null;

        // 쿠키가 존재하는지 확인 후 jwt 쿠키 값 설정
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    jwtCookie = cookie.getValue();
                    break;
                }
            }
        }

        // jwt 쿠키가 없다면 필터 체인 계속 진행
        if (jwtCookie == null) {
            filterChain.doFilter(request, response);
            return;
        }

        UserInfo userInfo;
        try {
            // jwtCookie를 UserInfo로 변환
            userInfo = JwtTokenUtil.extractToken(jwtCookie);
        } catch (Exception e) {
            //유효기간 만료되거나 이상할 경우
            filterChain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userInfo, // 인증 주체
                null,
                userInfo.getAuthorities() // 권한 정보
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);


        //요청들어올때마다 실행
        filterChain.doFilter(request, response);
    }

}
