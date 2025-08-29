package com.talearnt.user.infomation;

import com.talearnt.auth.login.LoginService;
import com.talearnt.enums.common.ErrorCode;
import com.talearnt.enums.common.Regex;
import com.talearnt.user.infomation.entity.User;
import com.talearnt.user.infomation.repository.UserQueryRepository;
import com.talearnt.user.infomation.repository.UserRepository;
import com.talearnt.user.infomation.request.TestChangePwdReqDTO;
import com.talearnt.user.infomation.response.UserActivityCountsResDTO;
import com.talearnt.user.infomation.response.UserHeaderResDTO;
import com.talearnt.user.infomation.request.WithdrawalRequestDTO;
import com.talearnt.user.infomation.response.WithdrawalCompletionResponseDTO;
import com.talearnt.enums.user.UserRole;
import com.talearnt.s3.S3Service;
import com.talearnt.stomp.firebase.repository.FcmTokenRepository;
import com.talearnt.stomp.notification.repository.NotificationSettingRepository;
import com.talearnt.user.talent.repository.MyTalentRepository;
import com.talearnt.user.talent.repository.MyTalentQueryRepository;
import com.talearnt.user.talent.MyTalentService;
import com.talearnt.util.common.UserUtil;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.jwt.UserInfo;
import com.talearnt.util.log.LogRunningTime;
import com.talearnt.util.response.CommonResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserService {


    private final PasswordEncoder passwordEncoder;

    //Repositories
    private final UserRepository userRepository;
    private final UserQueryRepository userQueryRepository;
    private final MyTalentQueryRepository myTalentQueryRepository;
    private final MyTalentService myTalentService;
    private final S3Service s3Service;
    private final FcmTokenRepository fcmTokenRepository;
    private final NotificationSettingRepository notificationSettingRepository;
    private final MyTalentRepository myTalentRepository;
    private final LoginService loginService;

    /**
     * FE에서 회원이 사용할 기본 정보와 키워드 설정이 되어 있는지 확인하는 값이 담겨있는 DTO를 반환한다.<br>
     * EX - 1) 디자인 페이지에서 로그인 했을 시에 헤더에 보일 프로필 이미지 및 닉네임<br>
     * EX - 2) 게시글 상세보기에서 수정, 삭제 버튼이 보일지에 대한 여부<br>
     * 조건 )
     * - 로그인이 되어 있어야 한다.
     */
    public UserHeaderResDTO getHeaderUserInfomation(Authentication authentication) {
        log.info("Header의 회원 기본 정보 불러오기 시작");

        //회원이 로그인한 상태인지 확인
        UserInfo userInfo = UserUtil.validateAuthentication("Header의 회원 기본 정보 불러오기", authentication);

        log.info("Header의 회원 기본 정보 불러오기 끝");
        return UserHeaderResDTO.builder()
                .userNo(userInfo.getUserNo())
                .userId(userInfo.getUserId())
                .nickname(userInfo.getNickname())
                .profileImg(userInfo.getProfileImg())
                .giveTalents(myTalentQueryRepository.getGiveTalentCodesByUserNo(userInfo.getUserNo()))
                .receiveTalents(myTalentQueryRepository.getReceiveTalentCodesByUserNo(userInfo.getUserNo()))
                .build();
    }


    /**
     * 테스트용 비밀번호 변경
     */
    public ResponseEntity<CommonResponse<String>> changeTestPwd(TestChangePwdReqDTO testChangePwdReqDTO) {
        log.info("테스트용 비밀번호 변경 시작 : {}", testChangePwdReqDTO);
        User user = userRepository.findByUserId(testChangePwdReqDTO.getUserId())
                .orElseThrow(() -> new CustomRuntimeException(ErrorCode.USER_NOT_FOUND));

        user.setPw(passwordEncoder.encode(testChangePwdReqDTO.getPw()));

        userRepository.save(user);
        log.info("테스트용 비밀번호 변경 끝");
        return CommonResponse.success("테스트 비밀번호 변경 성공");
    }


    /**
     * 회원의 프로필 수정
     * 조건)
     * - 로그인이 되어 있어야 한다.
     * - Valid 조건 충족
     * - 닉네임 중복 체크
     * - 수정할 키워드가 Talent 키워드가 존재하는지 확인
     * - Talent 키워드가 존재하지 않는다면, DB에 저장
     * - 비활성화일 경우에 활성화
     * - 활성화된 키워드와 수정할 Talent 키워드에 없으면 비활성화 처리
     */
    @Transactional
    @LogRunningTime
    public UserHeaderResDTO updateProfile(UserInfo userInfo, String nickname, String profileImg, List<Integer> giveTalents, List<Integer> receiveTalents) {
        log.info("회원 프로필 수정 시작 : {}", userInfo);

        //회원 정보 호출
        User user = userRepository.findByUserNo(userInfo.getUserNo())
                .orElseThrow(() -> {
                    log.error("회원 프로필 수정 실패 - 존재하지 않는 회원 : {}", userInfo.getUserNo());
                    return new CustomRuntimeException(ErrorCode.USER_NOT_FOUND);
                });

        //프로필 이미지가 null이 아니고, 파일 확장자가 잘못된 경우
        if(profileImg != null && !profileImg.matches(Regex.FILE_EXTENSION.getPattern())){
            log.error("회원 프로필 수정 실패 - 프로필 이미지 확장자 오류 : {}", profileImg);
            throw new CustomRuntimeException(ErrorCode.FILE_UPLOAD_TYPE_NOT_MATCH);
        }

        //닉네임 중복 확인 && 현재 닉네임과 요청한 닉네임이 다른 경우
        if (userRepository.existsByNickname(nickname) && !user.getNickname().equalsIgnoreCase(nickname)) {
            log.error("회원 프로필 수정 실패 - 닉네임 중복 : {}", nickname);
            throw new CustomRuntimeException(ErrorCode.DUPLICATE_USER_NICKNAME);
        }

        //user 닉네임이 바뀌었으면 변경
        if (!user.getNickname().equalsIgnoreCase(nickname)){
            user.setNickname(nickname);
        }
        //프로필 이미지가 바뀌었으면 변경
        if (!Objects.equals(user.getProfileImg(), profileImg)) {
            user.setProfileImg(profileImg);
        }
        //mytalent service 에서 Mytalents 변경
        myTalentService.updateMyTalents(userInfo, giveTalents, receiveTalents);


        log.info("회원 프로필 수정 끝");
        return UserHeaderResDTO.builder()
                .userNo(userInfo.getUserNo())
                .userId(userInfo.getUserId())
                .nickname(nickname)
                .profileImg(profileImg)
                .giveTalents(giveTalents)
                .receiveTalents(receiveTalents)
                .build();
    }

    @LogRunningTime
    public UserActivityCountsResDTO getMyActivityCounts(Authentication authentication) {
        log.info("회원 활동 카운트 조회 시작");

        //회원이 로그인한 상태인지 확인
        UserInfo userInfo = UserUtil.validateAuthentication("회원 활동 카운트 조회", authentication);

        //활동 카운트 조회
        UserActivityCountsResDTO activityCounts = userQueryRepository.getMyActivityCounts(userInfo.getUserNo());

        log.info("회원 활동 카운트 조회 끝");
        return activityCounts;
    }

    /**
     * 회원 탈퇴를 처리합니다.
     */
    @Transactional
    @LogRunningTime
    public WithdrawalCompletionResponseDTO processWithdrawal(HttpServletResponse response, UserInfo userInfo, WithdrawalRequestDTO request) {
        log.info("회원 탈퇴 처리 시작 : {}", userInfo.getUserId());
        
        // 사용자 정보 조회
        User user = userRepository.findByUserNo(userInfo.getUserNo())
                .orElseThrow(() -> new CustomRuntimeException(ErrorCode.USER_NOT_FOUND));
        
        // 이미 탈퇴한 사용자인지 확인
        if (user.getIsWithdrawn() != null && user.getIsWithdrawn()) {
            throw new CustomRuntimeException(ErrorCode.USER_WITH_DRAWN);
        }
        
        // 탈퇴 사유들을 쉼표로 구분하여 저장
        String withdrawalReasonCodes = String.join(",", request.getWithdrawalReasons());
        
        // 탈퇴 정보 저장
        user.setWithdrawalReasonCodes(withdrawalReasonCodes);
        user.setWithdrawalReason(request.getDetailedReason());
        user.setWithdrawnAt(LocalDateTime.now());
        user.setIsWithdrawn(true);
        user.setAuthority(UserRole.ROLE_WITHDRAWN);
        
        // 닉네임 중복 방지를 위해 원래 닉네임을 저장하고 새로운 닉네임 생성
        String originalNickname = user.getNickname();
        user.setWithdrawnNickname(originalNickname);
        user.setNickname("탈퇴회원_" + System.currentTimeMillis());

        // 아이디 중복 방지를 위해 원래 아이디를 저장하고 새로운 아이디 생성
        String originalUserId = user.getUserId();
        user.setWidthdrawnUserId(originalUserId);
        user.setUserId("withdrawn_" + System.currentTimeMillis());

        // 휴대폰 번호 중복 방지를 위해 원래 휴대폰 번호를 저장하고 새로운 번호 생성
        String originalPhone = user.getPhone();
        user.setWithdrawnPhoneNumber(originalPhone);
        user.setPhone("");

        
        // 프로필 이미지가 있다면 S3에서 삭제
        if (user.getProfileImg() != null && !user.getProfileImg().isEmpty()) {
            try {
                s3Service.deleteFiles(Set.of(user.getProfileImg()));
            } catch (Exception e) {
                log.warn("프로필 이미지 삭제 실패: {}", e.getMessage());
            }
        }
        
        // FCM 토큰 삭제
        fcmTokenRepository.deleteByUserNo(user.getUserNo());
        
        // 알림 설정 삭제
        notificationSettingRepository.deleteByUserUserNo(user.getUserNo());
        
        // 재능 키워드 삭제
        myTalentRepository.deleteByUserUserNo(user.getUserNo());
        
        // 사용자 정보 저장
        userRepository.save(user);

        //로그아웃 처리
        loginService.logout(response);

        log.info("회원 탈퇴 처리 완료 : {}", userInfo.getUserId());
        
        // 탈퇴 완료 응답 생성
        return WithdrawalCompletionResponseDTO.builder()
                .userId(userInfo.getUserId())
                .withdrawnAt(user.getWithdrawnAt())
                .build();
    }
}
