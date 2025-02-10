package com.talearnt.user.infomation;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.user.infomation.entity.User;
import com.talearnt.user.infomation.repository.UserRepository;
import com.talearnt.user.infomation.request.TestChangePwdReqDTO;
import com.talearnt.user.infomation.response.UserHeaderResDTO;
import com.talearnt.user.talent.repository.MyTalentQueryRepository;
import com.talearnt.user.talent.repository.MyTalentRepository;
import com.talearnt.util.common.UserUtil;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.jwt.UserInfo;
import com.talearnt.util.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserService {


    private final PasswordEncoder passwordEncoder;

    //Repositories
    private final UserRepository userRepository;
    private final MyTalentQueryRepository myTalentQueryRepository;

    /** FE에서 회원이 사용할 기본 정보와 키워드 설정이 되어 있는지 확인하는 값이 담겨있는 DTO를 반환한다.<br>
     * EX - 1) 디자인 페이지에서 로그인 했을 시에 헤더에 보일 프로필 이미지 및 닉네임<br>
     * EX - 2) 게시글 상세보기에서 수정, 삭제 버튼이 보일지에 대한 여부<br>
     * 조건 )
     * - 로그인이 되어 있어야 한다.
     * */
    public UserHeaderResDTO getHeaderUserInfomation(Authentication authentication){
        log.info("Header의 회원 기본 정보 불러오기 시작");

       //회원이 로그인한 상태인지 확인
        UserInfo userInfo = UserUtil.validateAuthentication("Header의 회원 기본 정보 불러오기",authentication);

        log.info("Header의 회원 기본 정보 불러오기 끝");
        return UserHeaderResDTO.builder()
                .userNo(userInfo.getUserNo())
                .nickname(userInfo.getNickname())
                .profileImg(userInfo.getProfileImg())
                .giveTalentCodes(myTalentQueryRepository.getGiveTalentCodesByUserNo(userInfo.getUserNo()))
                .receiveTalentCodes(myTalentQueryRepository.getReceiveTalentCodesByUserNo(userInfo.getUserNo()))
                .build();
    }



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
