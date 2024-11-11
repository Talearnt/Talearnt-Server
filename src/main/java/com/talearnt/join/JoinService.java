package com.talearnt.join;

import com.talearnt.enums.ErrorCode;
import com.talearnt.enums.UserRole;
import com.talearnt.user.entity.User;
import com.talearnt.user.repository.UserRepository;
import com.talearnt.util.common.UserUtil;
import com.talearnt.util.exception.CustomException;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.verification.VerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class JoinService {

    private final UserRepository userRepository;
    private final VerificationService verificationService;
    private final PasswordEncoder passwordEncoder;

    // 회원가입 서비스 메서드
    public ResponseEntity<CommonResponse<String>> registerUser(JoinReqDTO joinReqDTO) {
        log.info("Register User 시작");
        //해당 UserId에 본인 인증이 완료되지 않은 상태일 경우 발생
        if(!verificationService.isVerifiedCheck(joinReqDTO)) {
            new CustomRuntimeException(ErrorCode.UNVERIFIED_AUTH_CODE);
        }

        String encodedPassword = passwordEncoder.encode(joinReqDTO.getPw());

        //닉네임 존재 유무 확인
        String nickname = UserUtil.makeRandomNickName();
        while(userRepository.existsByNickname(nickname)){
            nickname = UserUtil.makeRandomNickName();
        }

        User user = JoinMapper.INSTANCE.toEntity(joinReqDTO);
        user.setPw(encodedPassword);
        user.setNickname(nickname);
        user.setJoinType("자사");
        user.setAuthority(UserRole.ROLE_USER);
        
        //저장
        userRepository.save(user);
        log.info("Register User 끝");
        return CommonResponse.success("회원가입 성공");
    }

    /**카카오톡 회원 가입
     * */
    public ResponseEntity<CommonResponse<String>> addKakaoUser(){

        return CommonResponse.success("회원 가입 성공");
    }


}
