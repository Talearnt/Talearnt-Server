package com.talearnt.user;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.user.entity.User;
import com.talearnt.user.repository.MyTalentRepository;
import com.talearnt.user.repository.UserRepository;
import com.talearnt.user.request.MyTalentsReqDTO;
import com.talearnt.user.request.TestChangePwdReqDTO;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserService {


    private final PasswordEncoder passwordEncoder;

    //Repositories
    private final UserRepository userRepository;
    private final MyTalentRepository myTalentRepository;

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

    /** 나의 재능 추가하기<br>
     * 조건<br>
     * - 로그인이 되어 있을 것 (컨트롤러에서 확인)<br>
     * - 등록된 재능 코드일 것
     * @param talents 나의 재능 키워드들
     *                type : <br>
     *                false : 주고 싶은<br>
     *                true : 받고 싶은
     * */
    public String addMyTalents(List<MyTalentsReqDTO> talents){
        log.info("나의 재능, 관심 있는 재능들 추가 시작 : {}",talents);

        //등록된 재능 코드인지 확인


        log.info("나의 재능, 관심 있는 재능들 추가 끝");
        return null;
    }

}
