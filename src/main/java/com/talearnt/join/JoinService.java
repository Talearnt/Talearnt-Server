package com.talearnt.join;

import com.talearnt.enums.UserRole;
import com.talearnt.user.entity.User;
import com.talearnt.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class JoinService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    // 회원가입 서비스 메서드
    public User registerUser(JoinReqDTO joinReqDTO) {
        log.info("Register User 시작");
        String encodedPassword = passwordEncoder.encode(joinReqDTO.getPw());

        User user = JoinMapper.INSTANCE.toEntity(joinReqDTO);
        user.setPw(encodedPassword);
        user.setNickname("");
        user.setJoinType("자사");
        user.setAuthority(UserRole.ROLE_USER);
        log.info("Register User 끝");
        return userRepository.save(user);
    }


}
