package com.talearnt.join;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class JoinService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper mapper;


    // 회원가입 서비스 메서드
    public User registerUser(JoinReqDTO joinReqDTO) {
        log.info("{},{},{}",joinReqDTO.getUserId(),joinReqDTO.getNickname(),joinReqDTO.getPhone());
        String encodedPassword = passwordEncoder.encode(joinReqDTO.getPw());

        JoinReqDTO changedDto = new JoinReqDTO.JoinReqDTOBuilder()
                .userId(joinReqDTO.getUserId())
                .pw(encodedPassword)
                .phone(joinReqDTO.getPhone())
                .joinType(joinReqDTO.getJoinType())
                .nickname("예시닉넴")
                .authority(joinReqDTO.getAuthority())
                .gender(joinReqDTO.getGender())
                .build();

        User user = mapper.map(changedDto,User.class);

        return userRepository.save(user);
    }


//    public boolean existsByUserId(String userId) {
//        return joinRepository.existsByUserId(userId);
//    }

}
