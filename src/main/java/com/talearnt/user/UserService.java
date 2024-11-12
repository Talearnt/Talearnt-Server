package com.talearnt.user;

import com.talearnt.enums.ErrorCode;
import com.talearnt.user.entity.User;
import com.talearnt.user.repository.UserQueryRepository;
import com.talearnt.user.repository.UserRepository;
import com.talearnt.user.request.CheckUserVerificationCodeReqDTO;
import com.talearnt.user.request.TestChangePwdReqDTO;
import com.talearnt.util.common.VerificationUtil;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.verification.Entity.PhoneVerification;
import com.talearnt.verification.Entity.PhoneVerificationTrace;
import com.talearnt.verification.VerificationMapper;
import com.talearnt.verification.repository.PhoneVerificationTraceRepository;
import com.talearnt.verification.repository.VerificationCodeQueryRepository;
import com.talearnt.verification.repository.VerificationCodeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserService {
    /**개선 할 부분, Verification Service에서도 동일한 코드가 발견되고 있음.*/
    @Value("${coolsms.fromNumber}")
    private String fromNumber;

    private final PasswordEncoder passwordEncoder;
    private final DefaultMessageService messageService;

    //Repositories
    private final UserRepository userRepository;
    private final UserQueryRepository userQueryRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final VerificationCodeQueryRepository verificationCodeQueryRepository;
    private final PhoneVerificationTraceRepository phoneVerificationTraceRepository;

    public ResponseEntity<CommonResponse<String>> changeTestPwd(TestChangePwdReqDTO testChangePwdReqDTO){
        User user = userRepository.findByUserId(testChangePwdReqDTO.getUserId())
                .orElseThrow(() -> new CustomRuntimeException(ErrorCode.USER_NOT_FOUND));

        user.setPw(passwordEncoder.encode(testChangePwdReqDTO.getPw()));

        userRepository.save(user);
        return CommonResponse.success("테스트 비밀번호 변경 성공");
    }

    /**아이디 찾기 인증 번호 발송*/
    public ResponseEntity<CommonResponse<String>> sendAuthenticationCode(String phoneNumber){
        log.info("유저 아이디 찾기 문자 전송 시작 : {}",phoneNumber);
        //유저의 아이디가 없기 때문에, 휴대폰 번호에 해당하는 유저를 가져오는 쿼리.
        String userId = userQueryRepository.selectUserByPhoneNumber(phoneNumber)
                .orElseThrow(()->new CustomRuntimeException(ErrorCode.USER_NOT_FOUND_PHONE_NUMBER));

        //랜덤 인증 코드 생성.
        String verificationCode = VerificationUtil.makeRandomVerificationNumber();
        //메세지 설정
        Message message = new Message();
        message.setFrom(fromNumber);
        message.setTo(phoneNumber);
        message.setText("Talearnt 인증번호는 [ " + verificationCode + " ] 입니다.");

        //인증 번호 셋팅하기.
        PhoneVerification phoneVerification = new PhoneVerification();
        phoneVerification.setIsPhoneVerified(false);
        phoneVerification.setVerificationCode(verificationCode);
        phoneVerification.setPhone(phoneNumber);
        phoneVerification.setUserId(userId);

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        log.info("휴대폰 문자 전송 후 데이터 : {}",response);

        //성공적으로 문자 전송 시에 인증 번호 데이터 베이스에 저장.
        if (response.getStatusCode().equals("2000")){
            verificationCodeRepository.save(phoneVerification);
        }

        log.info("유저 아이디 찾기 문자 전송 끝");
        return CommonResponse.success(response.getStatusMessage());
    }


    /**인증 번호 검증하는 단계.
     * 이곳에서 DB가 검증 컬럼의 값이 True로 바뀔 경우
     * 다른 곳에서 확인하여 작업할 수 있음.
     * 조건 :
     * 1. 10분 이내로 해당 휴대폰 번호로 만들어진 VerificationCode가 있는가?
     * 2. 있다면, 시도는 총 몇번을 했나?
     * 3. 5번 이상 했다면 해당 VerificationCode를 지우고 "10분 후 다시 시도 메세지 전송"
     * */
    public ResponseEntity<CommonResponse<Boolean>> checkVerificationCode(CheckUserVerificationCodeReqDTO reqDTO){
        log.info("유저 아이디 찾기 인증 번호 검증 시작 : {}",reqDTO);
        //10분 이내로 해당 번호로 발송된 인증 번호 가져오기.
        PhoneVerification phoneVerification = verificationCodeQueryRepository.selectPhoneVerificationByMinutesAndPhone(reqDTO.getPhone())
                .orElseThrow(()->{
                    log.info(ErrorCode.AUTH_NOT_FOUND_PHONE_CODE);
                    return new CustomRuntimeException(ErrorCode.AUTH_NOT_FOUND_PHONE_CODE);
                });
        //인증 번호 시도 횟수가 5번 이상일 경우 차단
        if (5 <= verificationCodeQueryRepository.countTryPhoneVerification(phoneVerification.getPhoneVerificationNo(),reqDTO.getPhone())){
            log.info("유저 찾기 아이디 인증 번호 검증 실패 - 5회 이상 시도 : {}",ErrorCode.AUTH_TOO_MANY_REQUEST);
            throw new CustomRuntimeException(ErrorCode.AUTH_TOO_MANY_REQUEST);
        }

        //인증 번호가 동일하지 않다면, PhoneVerificationTrace 테이블에 추가.
        if (!reqDTO.equals(phoneVerification.getVerificationCode())){
            PhoneVerificationTrace trace = VerificationMapper.INSTANCE.toTraceEntity(phoneVerification,reqDTO);
            phoneVerificationTraceRepository.save(trace);
            log.info("유저 찾기 아이디 인증 번호 검증 실패 - 인증 번호 같지 않음 : {}",trace);
            throw new CustomRuntimeException(ErrorCode.INVALID_AUTH_CODE);
        }

        //성공, 해당 코드 isPhoneVeified를 True 변환 후 저장 및 Trace 로그 삭제.
        phoneVerification.setIsPhoneVerified(true);
        verificationCodeRepository.save(phoneVerification);

        log.info("유저 아이디 찾기 인증 번호 검증 끝");
        return CommonResponse.success(true);
    }

}
