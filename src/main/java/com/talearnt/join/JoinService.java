package com.talearnt.join;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.enums.user.UserRole;
import com.talearnt.admin.agree.entity.Agree;
import com.talearnt.admin.agree.entity.AgreeCode;
import com.talearnt.admin.agree.repository.AgreeCodeRepository;
import com.talearnt.admin.agree.repository.AgreeRepository;
import com.talearnt.join.request.AgreeJoinReqDTO;
import com.talearnt.join.request.JoinReqDTO;
import com.talearnt.join.request.KakaoJoinReqDTO;
import com.talearnt.login.KakaoLoginService;
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

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class JoinService {

    private final UserRepository userRepository;
    private final AgreeRepository agreeRepository;
    private final AgreeCodeRepository agreeCodeRepository;
    private final VerificationService verificationService;
    private final PasswordEncoder passwordEncoder;
    private final KakaoLoginService kakaoLoginService;

    /**
     * 회원 가입 단계에서 아이디 중복체크
     */
    public ResponseEntity<CommonResponse<Boolean>> checkDuplicatedUserId(String userId) {
        return CommonResponse.success(userRepository.existsByUserId(userId));
    }

    /**
     * 최신 업데이트 일자 : 2024-11-26, 담당자 : 정운만 <br>
     * 업데이트 내용 : 회원가입 시 이미 존재하는 휴대폰 번호를 입력했을 경우 Exception 발생<br>
     */
    @Transactional
    public ResponseEntity<CommonResponse<String>> registerUser(JoinReqDTO joinReqDTO) {
        log.info("Register User 시작");
        //해당 UserId에 본인 인증이 완료되지 않은 상태일 경우 발생
        if (!verificationService.isVerifiedCheck(joinReqDTO)) {
            new CustomRuntimeException(ErrorCode.UNVERIFIED_AUTH_CODE);
        }

        //비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(joinReqDTO.getPw());

        //값을 저장하기 위해 DTO -> Entity로 변경
        User user = JoinMapper.INSTANCE.toEntity(joinReqDTO);
        user.setPw(encodedPassword);

        //유저 회원가입 절차, 중복,
        user = saveUser(user, "자사");

        //이용 약관 저장
        saveTerms(joinReqDTO.getAgreeReqDTOS(), user);

        log.info("Register User 끝");
        return CommonResponse.success("회원가입 성공");
    }

    /**
     * 카카오톡 회원 가입은 본인인증을 이미 Kakao에서 거친 것과 다름이 없기 때문에<br>
     * verificationService.isVerifiedCheck(joinReqDTO)을 따로 하지 않는다.<br>
     *
     * @param kakaoJoinReqDTO 카카오 회원가입에서 넘어온 값
     */
    @Transactional
    public ResponseEntity<CommonResponse<String>> addKakaoUser(KakaoJoinReqDTO kakaoJoinReqDTO) {
        log.info("카카오톡 회원가입 시작 : {}", kakaoJoinReqDTO);


        //카카오 회원가입에서 넘어온 값 Entity로 변경
        User user = JoinMapper.INSTANCE.toUserEntityFromKakaoJoinReqDTO(kakaoJoinReqDTO);

        user = saveUser(user, "카카오톡");

        //이용 약관 저장
        saveTerms(kakaoJoinReqDTO.getAgreeReqDTOS(), user);

        log.info("카카오톡 회원가입 끝");
        return CommonResponse.success("카카오톡 회원 가입 성공");
    }

    /**
     * 유저 회원가입 메소드<br>
     * 조건<br>
     * - 중복된 아이디가 없어야 한다.<br>
     * - 중복된 닉네임이 없어야 한다.
     *
     * @param user     DTO에서 변환된 Entity
     * @param joinType 카카오,네이버,자사
     */
    private User saveUser(User user, String joinType) {
        //이미 가입한 휴대폰 번호가 있는 지 검증 Exception 409
        UserUtil.validatePhoneDuplication(userRepository, user.getPhone());
        //유저 ID가 있을 경우에 회원 가입 실패
        UserUtil.validateDuplicateUserId(userRepository, user.getUserId());
        //닉네임 존재 유무 확인
        String nickname = UserUtil.makeRandomNickName();
        while (userRepository.existsByNickname(nickname)) {
            nickname = UserUtil.makeRandomNickName();
        }
        user.setNickname(nickname);
        user.setJoinType(joinType);
        user.setAuthority(UserRole.ROLE_USER);

        //유저 저장
        user = userRepository.save(user);

        return user;
    }

    /**
     * 이용약관 등록
     */
    private void saveTerms(List<AgreeJoinReqDTO> agreeJoinReqDTOS, User user) {
        //이용 약관 저장
        for (AgreeJoinReqDTO agreeReqDTO : agreeJoinReqDTOS) {
            //약관 코드 ID가 없을 경우 Exception
            AgreeCode agreeCode = agreeCodeRepository.findById(agreeReqDTO.getAgreeCodeId())
                    .orElseThrow(() -> new CustomRuntimeException(ErrorCode.USER_NOT_FOUND_AGREE));

            //필수 약관을 동의하지 않았을 경우
            if (agreeCode.isMandatory() == true && !agreeReqDTO.isAgree()) {
                log.error("약관 동의 여부 : {}", agreeReqDTO);
                throw new CustomRuntimeException(ErrorCode.USER_REQUIRED_NOT_AGREE);
            }

            //동의한 약관 저장.
            Agree agree = JoinMapper.INSTANCE.toAgreeEntity(agreeReqDTO);
            agree.setUser(user);
            agreeRepository.save(agree);
        }
    }


}
