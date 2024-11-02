package com.talearnt.login;

import com.talearnt.enums.ErrorCode;
import com.talearnt.join.User;
import com.talearnt.join.UserRepository;
import com.talearnt.util.exception.CustomException;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.jwt.JwtTokenUtil;
import com.talearnt.util.jwt.TokenResDTO;
import com.talearnt.util.jwt.UserInfo;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Log4j2
@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final ModelMapper mapper;

    public TokenResDTO authenticateUser(LoginReqDTO loginReqDTO, HttpServletResponse response) {
        // DB에서 사용자 조회
        User user = userRepository.findByUserId(loginReqDTO.getUserId());
        // passwordEncoder 막아놈
//        if (user == null || !passwordEncoder.matches(loginReqDTO.getPw(), user.getPw())) {
//            throw new CustomRuntimeException(ErrorCode.INVALID_CREDENTIALS);
//        }

        if (user == null || !loginReqDTO.getPw().equals(user.getPw())) {
            throw new CustomRuntimeException(ErrorCode.USER_NOT_FOUND);
        }


        // 인증 객체 생성 및 인증 수행
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(loginReqDTO.getUserId(), loginReqDTO.getPw());

        //인증 작업 완료 후 UserInfo로 변환
        UserInfo userInfo = mapper.map(user, UserInfo.class);

        //리프레시 토큰 생성
        String refreshToken = jwtTokenUtil.createRefreshToken(userInfo);

        // 리프레시 토큰 쿠키에 설정
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 유효기간 7일
        // 응답에 쿠키 추가
        response.addCookie(cookie);

        return new TokenResDTO(jwtTokenUtil.createJwtToken(userInfo));
    }


    /** 리프레시 토큰이 유효하면 다시 JWT 토큰 발급하기*/
    public TokenResDTO refreshJwtToken(String refreshToken) throws CustomException {
        String userId = jwtTokenUtil.extractUserId(refreshToken);
        User user = userRepository.findByUserId(userId);
        UserInfo userInfo = mapper.map(user, UserInfo.class);
        if (jwtTokenUtil.isTokenValid(refreshToken,userInfo)){
            return TokenResDTO.builder().accessToken(jwtTokenUtil.createJwtToken(userInfo)).build();
        }

        throw new CustomException(ErrorCode.INVALID_TOKEN);

    }
}
