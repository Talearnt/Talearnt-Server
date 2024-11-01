package com.talearnt.util.jwt;

import com.talearnt.enums.UserRole;
import com.talearnt.join.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    static private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    // JWT 만들어주는 함수
    public static String createToken(Authentication auth) {
        User user = (User) auth.getPrincipal();
        auth.getAuthorities().stream().map(a -> a.getAuthority())
                .collect(Collectors.joining(","));

        String jwt = Jwts.builder()
                .claim("userNo",user.getUserNo())
                .claim("userId", user.getUserId())
                .claim("profileImg",user.getProfileImg())
                .claim("nickname", user.getNickname())
                .claim("authority",user.getAuthority())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 86400000)) //유효기간 24시간
                .signWith(key)
                .compact();
        return jwt;
    }



    public static UserInfo extractToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        long userNo = claims.get("userNo", Long.class);
        String userId = claims.get("userId", String.class);
        String nickname = claims.get("nickname", String.class);
        String profileImg = claims.get("profileImg", String.class);
        UserRole authority = UserRole.valueOf(claims.get("authority", String.class)); // String을 UserRole로 변환

        return new UserInfo(userNo, userId, nickname, profileImg, authority);
    }

}
