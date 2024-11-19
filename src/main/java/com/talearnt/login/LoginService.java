package com.talearnt.login;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.login.company.LoginMapper;
import com.talearnt.login.company.LoginReqDTO;
import com.talearnt.user.entity.User;
import com.talearnt.user.repository.UserRepository;
import com.talearnt.util.common.LoginUtil;
import com.talearnt.util.exception.CustomException;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.jwt.JwtTokenUtil;
import com.talearnt.util.jwt.TokenResDTO;
import com.talearnt.util.jwt.UserInfo;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Log4j2
@Service
@RequiredArgsConstructor
public class LoginService {
    @Value("${kakao.pwd}")
    private String pwd;

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    public TokenResDTO authenticateUser(LoginReqDTO loginReqDTO, HttpServletResponse response) {
        log.info("자사 로그인 서비스 시작 : {}",loginReqDTO);
        // DB에서 사용자 조회
        User user = userRepository.findByUserId(loginReqDTO.getUserId())
                .orElseThrow(()->new CustomRuntimeException(ErrorCode.INVALID_CREDENTIALS));

        //회원가입을 이미 했지만, 자사가 아닌 네이버나 카카오톡으로 회원가입을 했을 경우
        LoginUtil.validateJoinType(user,"자사");

        // passwordEncoder
        if (user == null || !passwordEncoder.matches(loginReqDTO.getPw(), user.getPw())) {
            log.info("자사 로그인 서비스 실패 - 비밀번호 불일치 : {}",ErrorCode.INVALID_CREDENTIALS);
            throw new CustomRuntimeException(ErrorCode.INVALID_CREDENTIALS);
        }

        //인증 후 RefreshToken 발급
        UserInfo userInfo = checkLoginValueAndSetRefreshToekn(user,response);

        log.info("자사 로그인 서비스 끝");
        return new TokenResDTO(jwtTokenUtil.createJwtToken(userInfo));
    }


    /** 리프레시 토큰이 유효하면 다시 JWT 토큰 발급하기*/
    public TokenResDTO refreshJwtToken(String refreshToken) throws CustomException {
        String userId = jwtTokenUtil.extractUserId(refreshToken);
        User user = userRepository.findByUserId(userId)
                .orElseThrow(()->new CustomRuntimeException(ErrorCode.INVALID_TOKEN));
        UserInfo userInfo = LoginMapper.INSTANCE.toUserInfo(user);
        if (jwtTokenUtil.isTokenValid(refreshToken,userInfo)){
            return TokenResDTO.builder().accessToken(jwtTokenUtil.createJwtToken(userInfo)).build();
        }

        throw new CustomException(ErrorCode.INVALID_TOKEN);
    }


    /** 카카오톡 로그인 중복 로직 분리<br>
     * 이것을 실행하기 전에 User Entity를 뽑아 와야 한다.<br>
     * @param user user의 정보가 담긴 Entity
     * @param response Cookie에 Refresh토큰 셋팅
     */
    public UserInfo checkLoginValueAndSetRefreshToekn(User user, HttpServletResponse response){
        user.setLastLogin(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
        userRepository.save(user);

        //KAKAO나 Naver는 PWD가 NULL이라서 오류 뜰 가능성 있어 임의의 값 넣고 있으면 해당 값으로 변경
        if (user.getPw() != null) {
            pwd = user.getPw();
        }
        // 인증 객체 생성 및 인증 수행
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(user.getUserId(), pwd);

        //인증 작업 완료 후 UserInfo로 변환
        UserInfo userInfo = LoginMapper.INSTANCE.toUserInfo(user);

        log.info("로그인 UserInfo : {}", userInfo);

        //리프레시 토큰 생성
        String refreshToken = jwtTokenUtil.createRefreshToken(userInfo);

        // 리프레시 토큰 쿠키에 설정
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 유효기간 7일
        // 응답에 쿠키 추가
        response.addCookie(cookie);
        log.info("Refresh Token : {}",refreshToken);
        log.info("Refresh Response : {}",response);
        log.info("Refresh Cookie : {}",cookie);
        return userInfo;
    }


}
