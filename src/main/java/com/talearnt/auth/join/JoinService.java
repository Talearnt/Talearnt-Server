package com.talearnt.auth.join;

import com.talearnt.auth.join.request.AgreeJoinReqDTO;
import com.talearnt.enums.common.ErrorCode;
import com.talearnt.enums.user.UserRole;
import com.talearnt.admin.agree.entity.Agree;
import com.talearnt.admin.agree.entity.AgreeCode;
import com.talearnt.admin.agree.repository.AgreeCodeRepository;
import com.talearnt.admin.agree.repository.AgreeRepository;
import com.talearnt.auth.join.request.JoinReqDTO;
import com.talearnt.auth.join.request.KakaoJoinReqDTO;
import com.talearnt.user.infomation.entity.User;
import com.talearnt.user.infomation.repository.UserRepository;
import com.talearnt.util.common.UserUtil;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.auth.verification.VerificationService;
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

    /**아이디 중복 체크*/
    public ResponseEntity<CommonResponse<Boolean>> checkDuplicatedUserId(String userId) {
        log.info("아이디 중복 체크 시작과 동시에 끝 : {} ",userId);
        return CommonResponse.success(userRepository.existsByUserId(userId));
    }

    /**닉네임 중복 체크*/
    public Boolean checkDuplicatedNickname(String nickname){
        log.info("닉네임 중복 체크 시작과 동시에 끝 : {} ",nickname);
        return userRepository.existsByNickname(nickname);
    }

    /** 랜덤한 닉네임 생성 후 뿌려주는 메소드 */
    public String makeRandomNickname(){
        String madeNickname = UserUtil.makeRandomNickName();
        while (checkDuplicatedNickname(madeNickname)){
            madeNickname = UserUtil.makeRandomNickName();
        }
        return madeNickname;
    }


    /**
     * 최신 업데이트 일자 : 2024-11-26, 담당자 : 정운만 <br>
     * 업데이트 내용 : 회원가입 시 이미 존재하는 휴대폰 번호를 입력했을 경우 Exception 발생<br>
     */
    @Transactional
    public ResponseEntity<CommonResponse<String>> registerUser(JoinReqDTO joinReqDTO) {
        log.info("Register User 시작 : {}",joinReqDTO);
        //해당 UserId에 본인 인증이 완료되지 않은 상태일 경우 발생
        if (!verificationService.isVerifiedCheck(joinReqDTO)) {
            log.error("Register User 실패 - 인증번호 불일치 : {} ",ErrorCode.UNVERIFIED_AUTH_CODE);
            throw new CustomRuntimeException(ErrorCode.UNVERIFIED_AUTH_CODE);
        }
        if (joinReqDTO.getPw().equals(joinReqDTO.getPwCheck())){
            log.error("Register User 실패 - 두 개의 비밀번호가 일치하지 않음 : {}",ErrorCode.USER_PASSWROD_FAILED_DOUBLE_CHECK);
            throw new CustomRuntimeException(ErrorCode.USER_PASSWROD_FAILED_DOUBLE_CHECK);
        }

        //비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(joinReqDTO.getPw());

        //값을 저장하기 위해 DTO -> Entity로 변경
        User user = JoinMapper.INSTANCE.toEntity(joinReqDTO);
        log.info("Register User user : {}",user);
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
     * @param user     DTO에서 변환된 Entity
     * @param joinType 카카오,네이버,자사
     */
    private User saveUser(User user, String joinType) {
        //이미 가입한 휴대폰 번호가 있는 지 검증 Exception 409
        UserUtil.validatePhoneDuplication(userRepository, user.getPhone());
        //유저 ID가 있을 경우에 회원 가입 실패
        UserUtil.validateDuplicateUserId(userRepository, user.getUserId());

        user.setJoinType(joinType);
        user.setAuthority(UserRole.ROLE_USER);

        //유저 저장
        user = userRepository.save(user);

        return user;
    }

    /**
     * 이용약관 등록<br>
     * 개선 점 for문을 돌면서 Save를 하고 있는데,<br>
     * 나중에는 모든 활성화된 이용약관을 가져오고, For문으로 유효성 검사를 거친다음<br>
     * DB에 저장하는 것으로 변경
     */
    private void saveTerms(List<AgreeJoinReqDTO> agreeJoinReqDTOS, User user) {
        log.info("이용 약관 동의 등록 시작 : {}, {}",agreeJoinReqDTOS,user);
        //이용 약관 저장
        for (AgreeJoinReqDTO agreeReqDTO : agreeJoinReqDTOS) {
            //약관 코드 ID가 없을 경우 Exception
            AgreeCode agreeCode = agreeCodeRepository.findById(agreeReqDTO.getAgreeCodeId())
                    .orElseThrow(() -> new CustomRuntimeException(ErrorCode.USER_NOT_FOUND_AGREE));

            //등록 또는 활성화 되지 않은 이용약관 ID가 넘어왔을 경우
            if(!agreeCode.isActive()){
                log.error("이용 약관 동의 등록 실패 - 유효하지 않은 이용약관 ID가 넘어옴 : {}",ErrorCode.TERMS_INVALID_VERSION);
                throw new CustomRuntimeException(ErrorCode.TERMS_INVALID_VERSION);
            }

            //필수 약관을 동의하지 않았을 경우
            if (agreeCode.isMandatory() && !agreeReqDTO.isAgree()) {
                log.error("이용 약관 동의 등록 실패 - 필수 약관 동의 안함 : {}", agreeReqDTO);
                throw new CustomRuntimeException(ErrorCode.USER_REQUIRED_NOT_AGREE);
            }

            //동의한 약관 저장.
            Agree agree = JoinMapper.INSTANCE.toAgreeEntity(agreeReqDTO);
            agree.setUser(user);
            agreeRepository.save(agree);
            log.info("이용 약관 동의 등록 끝");
        }
    }


}
