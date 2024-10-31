package com.talearnt.login;

import com.talearnt.join.User;
import com.talearnt.join.UserRepository;
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
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;

    public String login(@RequestBody LoginReqDTO loginReqDTO) throws AuthenticationException {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginReqDTO.getUserId(), loginReqDTO.getPw())
        );
        String inputPw = passwordEncoder.encode(loginReqDTO.getPw());
        User user = userRepository.findByUserId(loginReqDTO.getUserId());

        if(user.getPw().equals(inputPw)){
            return jwtTokenUtil.createToken(authentication);
        }else {
            // Exception 변경 예정
            throw new BadCredentialsException("Invalid username or password");
        }

    }
}
