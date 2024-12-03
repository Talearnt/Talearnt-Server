package com.talearnt.auth.verification;


import com.talearnt.auth.verification.repository.VerificationCodeQueryRepository;
import com.talearnt.enums.common.ErrorCode;
import com.talearnt.auth.join.request.JoinReqDTO;
import com.talearnt.user.infomation.repository.UserRepository;
import com.talearnt.auth.find.request.CheckUserVerificationCodeReqDTO;
import com.talearnt.auth.find.request.FindByPhoneReqDTO;
import com.talearnt.util.common.UserUtil;
import com.talearnt.util.common.VerificationUtil;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.auth.verification.Entity.PhoneVerification;
import com.talearnt.auth.verification.repository.VerificationCodeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class VerificationService {
    /**개선 할 부분, User Service에서도 동일한 코드가 발견되고 있음.*/
    @Value("${coolsms.fromNumber}")
    private String fromNumber;

    private final DefaultMessageService messageService;
    private final VerificationCodeRepository verificationCodeRepository;
    private final VerificationCodeQueryRepository verificationCodeQueryRepository;
    private final UserRepository userRepository;

    // coolsms 인증 문자 보내기
    @Transactional
    public ResponseEntity<CommonResponse<String>> sendVerificationMessage(FindByPhoneReqDTO findByPhoneReqDTO) {
        log.info("SMS 인증 문자 전송 시작 : {}",findByPhoneReqDTO);
        //이미 가입한 휴대폰 번호가 있을 경우 Exception 발생
        UserUtil.validatePhoneDuplication(userRepository,findByPhoneReqDTO.getPhone());

        //인증 코드 설정
        String verificationCode = VerificationUtil.makeRandomVerificationNumber();

        Message message = new Message();

        message.setFrom(fromNumber);
        message.setTo(findByPhoneReqDTO.getPhone());
        message.setText("Talearnt 인증번호는 [ " + verificationCode + " ] 입니다.");

        PhoneVerification phoneVerification = VerificationMapper.INSTANCE.toPhoneVerification(findByPhoneReqDTO);
        phoneVerification.setIsPhoneVerified(false);
        phoneVerification.setVerificationCode(verificationCode);
        verificationCodeRepository.save(phoneVerification);

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));

        log.info("SMS 인증 문자 전송 끝");
        return CommonResponse.success(response.getStatusMessage());
    }

    // phone_verification table의 code 검증
    @Transactional
    public Boolean verifyCode(CheckUserVerificationCodeReqDTO verifyCodeReqDTO) {
        log.info("SMS 인증 문자 검증 시작 : {}",verifyCodeReqDTO);
        // 10분 이내로 전송된 인증 코드가 있는가?
        PhoneVerification phoneVerification = verificationCodeQueryRepository.selectPhoneVerificationByMinutesAndPhone(verifyCodeReqDTO.getPhone())
                .orElseThrow(()->{
                    log.error(ErrorCode.AUTH_NOT_FOUND_PHONE_CODE);
                    return new CustomRuntimeException(ErrorCode.AUTH_NOT_FOUND_PHONE_CODE);
                });

        // 검증: 클라이언트가 입력한 코드와 DB의 코드 비교
        if (!phoneVerification.getVerificationCode().equals(verifyCodeReqDTO.getCode())) {
            log.error("SMS 인증 문자 검증 실패 - 인증 코드 불일치 : {}",ErrorCode.INVALID_AUTH_CODE);
            throw new CustomRuntimeException(ErrorCode.INVALID_AUTH_CODE);
        }

        //인증 성공하면 isPhoneVerified를 true로 변경
        phoneVerification.setIsPhoneVerified(true);
        verificationCodeRepository.save(phoneVerification);
        log.info("SMS 인증 문자 검증 끝");
        return true;
    }

    // 해당 userId에 본인 인증이 된 상태인지 check
    public Boolean isVerifiedCheck(JoinReqDTO joinReqDTO){
        return verificationCodeQueryRepository.checkIsPhoneVerified(joinReqDTO.getPhone())
                .orElseThrow(()->{
                    log.error(ErrorCode.AUTH_NOT_FOUND_PHONE_CODE);
                    return new CustomRuntimeException(ErrorCode.AUTH_NOT_FOUND_PHONE_CODE);
                });
    }

}
