package com.talearnt.util.jwt;

import com.talearnt.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Log4j2
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value(("${jwt.jwtTokenMilliseconds}"))
    private long jwtTokenMilliseconds;

    @Value(("${jwt.refreshTokenMilliseconds}"))
    private long refreshTokenMilliseconds;

    //JWT 토큰 생성
    public String createJwtToken(UserInfo userInfo) {
        return Jwts.builder()
                .claim("userNo",userInfo.getUserNo())
                .claim("profileImg",userInfo.getProfileImg())
                .claim("nickname", userInfo.getNickname())
                .claim("authority",userInfo.getAuthority().name())
                .setSubject(userInfo.getUserId()) // 토큰의 주체(사용자 이름)
                .setIssuedAt(new Date(System.currentTimeMillis())) // 토큰 발행 시간
                .setExpiration(new Date(System.currentTimeMillis() + jwtTokenMilliseconds)) // 토큰 만료 시간
                .signWith(SignatureAlgorithm.HS256, secretKey) // 서명 알고리즘 및 비밀 키 설정
                .compact(); // 토큰 압축 및 반환
    }

    // Refresh 토큰 생성
    public String createRefreshToken(UserInfo userInfo) {
        return Jwts.builder()
                .setSubject(userInfo.getUserId())
                .claim("authority",userInfo.getAuthority().name())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenMilliseconds))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // 클레임을 추출하는 메서드
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 유저 ID 추출
    public String extractUserId(String token) {
        return extractClaims(token).getSubject();
    }

    // 권한 추출
    public UserRole extractAuthority(String token) {
        String role = extractClaims(token).get("authority", String.class);

        return UserRole.valueOf(role);
    }

    // 토큰 유효성 확인
    public boolean isTokenValid(String token, UserInfo userInfo) {
        String userId = extractUserId(token);
        UserRole authority = extractAuthority(token);

        return (userId.equals(userInfo.getUserId()) && authority.equals(userInfo.getAuthority()) && !isTokenExpired(token));
    }

    // 토큰이 만료되었는지 확인하는 메서드
    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }


}
