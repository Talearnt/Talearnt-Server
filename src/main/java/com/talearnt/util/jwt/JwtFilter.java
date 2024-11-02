package com.talearnt.util.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserInfoService userInfoService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        String jwtToken = null;
        String userId = null;


        //Bearer 에서 토큰 값 추출 후 userId 셋팅
        if (header != null && header.startsWith("Bearer ")) {
            jwtToken = header.substring(7);
            userId = jwtTokenUtil.extractUserId(jwtToken);
        }

        // Jwt 토큰은 있지만 JWT Context에 등록이 되어있지 않은 경우
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserInfo userInfo = (UserInfo) userInfoService.loadUserByUsername(userId);

            //Jwt 토큰이 유효할 경우 등록
            if (jwtTokenUtil.isTokenValid(jwtToken, userInfo)) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userInfo, null, userInfo.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
