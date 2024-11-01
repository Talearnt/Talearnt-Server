package com.talearnt.login;

import com.talearnt.enums.ErrorCode;
import com.talearnt.join.User;
import com.talearnt.join.UserRepository;
import com.talearnt.util.exception.CustomException;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.jwt.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;


@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public Authentication authenticateUser(LoginReqDTO loginReqDTO) {
        // DB에서 사용자 조회
        User user = userRepository.findByUserId(loginReqDTO.getUserId());
        // passwordEncoder 막아놈
//        if (user == null || !passwordEncoder.matches(loginReqDTO.getPw(), user.getPw())) {
//            throw new CustomRuntimeException(ErrorCode.INVALID_CREDENTIALS);
//        }

        if (user == null || !loginReqDTO.getPw().equals(user.getPw())) {
            throw new CustomRuntimeException(ErrorCode.INVALID_CREDENTIALS);
        }

        // 인증 객체 생성 및 인증 수행
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(loginReqDTO.getUserId(), loginReqDTO.getPw());
        return authenticationManager.authenticate(authToken); // 인증 후 Authentication 반환
    }

}
