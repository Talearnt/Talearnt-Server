package com.talearnt.join;

import com.talearnt.enums.ErrorCode;
import com.talearnt.enums.Regex;
import com.talearnt.enums.UserRole;
import com.talearnt.admin.agree.entity.Agree;
import com.talearnt.admin.agree.entity.AgreeCode;
import com.talearnt.admin.agree.repository.AgreeCodeRepository;
import com.talearnt.admin.agree.repository.AgreeRepository;
import com.talearnt.join.request.AgreeJoinReqDTO;
import com.talearnt.join.request.JoinReqDTO;
import com.talearnt.user.entity.User;
import com.talearnt.user.repository.UserRepository;
import com.talearnt.util.common.UserUtil;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.verification.VerificationService;
import jakarta.transaction.Transactional;
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
    private final AgreeRepository agreeRepository;
    private final AgreeCodeRepository agreeCodeRepository;
    private final VerificationService verificationService;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<CommonResponse<Boolean>> checkDuplicatedUserId(String userId){

        return CommonResponse.success(userRepository.existsByUserId(userId));
    }

    // 회원가입 서비스 메서드
    @Transactional
    public ResponseEntity<CommonResponse<String>> registerUser(JoinReqDTO joinReqDTO) {
        log.info("Register User 시작");
        //해당 UserId에 본인 인증이 완료되지 않은 상태일 경우 발생
        if (!verificationService.isVerifiedCheck(joinReqDTO)) {
            new CustomRuntimeException(ErrorCode.UNVERIFIED_AUTH_CODE);
        }
        //유저 ID가 있을 경우에 회원 가입 실패
        if (!userRepository.existsByUserId(joinReqDTO.getUserId())) {
            new CustomRuntimeException(ErrorCode.DUPLICATE_USER_ID);
        }

        String encodedPassword = passwordEncoder.encode(joinReqDTO.getPw());

        //닉네임 존재 유무 확인
        String nickname = UserUtil.makeRandomNickName();
        while (userRepository.existsByNickname(nickname)) {
            nickname = UserUtil.makeRandomNickName();
        }

        User user = JoinMapper.INSTANCE.toEntity(joinReqDTO);
        user.setPw(encodedPassword);
        user.setNickname(nickname);
        user.setJoinType("자사");
        user.setAuthority(UserRole.ROLE_USER);

        //유저 저장
        user = userRepository.save(user);

        //이용 약관 저장
        for (AgreeJoinReqDTO agreeReqDTO : joinReqDTO.getAgreeReqDTOS()) {
            //약관 코드 ID가 없을 경우 Exception
            AgreeCode agreeCode = agreeCodeRepository.findById(agreeReqDTO.getAgreeCodeId())
                    .orElseThrow(() -> new CustomRuntimeException(ErrorCode.USER_NOT_FOUND_AGREE));

            //필수 약관을 동의하지 않았을 경우
            if(agreeCode.isMandatory()==true && !agreeReqDTO.isAgree()){
                log.error("약관 동의 여부 : {}",agreeReqDTO);
                throw new CustomRuntimeException(ErrorCode.USER_REQUIRED_NOT_AGREE);
            }

            //동의한 약관 저장.
            Agree agree = JoinMapper.INSTANCE.toAgreeEntity(agreeReqDTO);
            agree.setUser(user);
            agreeRepository.save(agree);
        }

        log.info("Register User 끝");
        return CommonResponse.success("회원가입 성공");
    }

    /**
     * 카카오톡 회원 가입
     */
    public ResponseEntity<CommonResponse<String>> addKakaoUser() {

        return CommonResponse.success("회원 가입 성공");
    }


}
