package com.talearnt.user.infomation;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.user.infomation.entity.User;
import com.talearnt.user.infomation.repository.UserRepository;
import com.talearnt.user.infomation.request.TestChangePwdReqDTO;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserService {


    private final PasswordEncoder passwordEncoder;

    //Repositories
    private final UserRepository userRepository;

    /** 테스트용 비밀번호 변경*/
    public ResponseEntity<CommonResponse<String>> changeTestPwd(TestChangePwdReqDTO testChangePwdReqDTO){
        log.info("테스트용 비밀번호 변경 시작 : {}",testChangePwdReqDTO);
        User user = userRepository.findByUserId(testChangePwdReqDTO.getUserId())
                .orElseThrow(() -> new CustomRuntimeException(ErrorCode.USER_NOT_FOUND));

        user.setPw(passwordEncoder.encode(testChangePwdReqDTO.getPw()));

        userRepository.save(user);
        log.info("테스트용 비밀번호 변경 끝");
        return CommonResponse.success("테스트 비밀번호 변경 성공");
    }



}
