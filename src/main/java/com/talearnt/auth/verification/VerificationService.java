package com.talearnt.auth.verification;


import com.talearnt.auth.verification.Entity.IpTrace;
import com.talearnt.auth.verification.repository.IpTraceRepository;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class VerificationService {
    /**개선 할 부분, User Service에서도 동일한 코드가 발견되고 있음.*/
    @Value("${coolsms.fromNumber}")
    private String fromNumber;


    //인증 Repo,Services
    private final DefaultMessageService messageService;
    private final VerificationCodeRepository verificationCodeRepository;
    private final VerificationCodeQueryRepository verificationCodeQueryRepository;
    private final UserRepository userRepository;
    private final IpTraceRepository ipTraceRepository;

    /**1분 이내 5번 요청했을 경우에 아이피 10분간 차단하는 METHOD
     * */
    public boolean isAllowedIp(String ip){
        log.info("SMS 요청 아이피 5회 이하 요청 검증 시작 : {}",ip);
        //DB에 아이피 값 가져오기
        Optional<IpTrace> optional = ipTraceRepository.findByIp(ip);

        //시간 값 설정
        LocalDateTime currentTime = LocalDateTime.now();//현재 시간
        long limitMinutes = 1; // 1분 이내 6번 이상 요청했을 경우 검증하기 위한 변수
        long blockMinutes = 10; //10분이 지났을 경우 차단 해제 하기 위한 변수

        //아이피가 있을 경우 Update
        if (optional.isPresent()){
            IpTrace ipEntity = optional.get();

            // 현재 시간과 마지막 요청 시간의 사이 값을 구한 변수
            long betweenMintues = Duration.between(ipEntity.getLastRequestTime(), currentTime).toMinutes();

            //횟수 6번 이상인 IP가 요청을 10분이 지나기 전에 요청을 하면 차단
            if ( ipEntity.getRequestCount() >= 5 && betweenMintues <= blockMinutes ){
                log.info("SMS 요청 아이피 5회 이하 요청 검증 실패 - 10분이 지나기 전에 요청이 들어옴");
                return false;
            }

            //10분이 지났을 경우 차단 해제 및 초기화
            if (betweenMintues > blockMinutes){
                ipEntity.setRequestCount(1);
                ipEntity.setLastRequestTime(currentTime);
                ipTraceRepository.save(ipEntity);
                log.info("SMS 요청 아이피 5회 이하 요청 검증 끝 - 10분이 지나 차단 해제");
                return true;
            }

            //1분 내로
            if (betweenMintues <= limitMinutes){
                //요청 횟수 증가
                ipEntity.setRequestCount(ipEntity.getRequestCount()+1);

                //요청횟수가 6번 이상일 경우 차단
                if(ipEntity.getRequestCount() > 5){
                    log.info("SMS 요청 아이피 5회 이하 요청 검증 실패 - 1분 내로 6번 이상 요청이 들어옴");
                    return false;
                }
            } else { //1분이 지났다면
                //요청 횟수 초기화
                ipEntity.setRequestCount(1);
            }
            
            //요청할 수 있음
            log.info("SMS 요청 아이피 5회 이하 요청 검증 끝 - 기존에 있던 아이피에서 요청이 들어옴");
            ipEntity.setLastRequestTime(currentTime);
            ipTraceRepository.save(ipEntity);
            return true;

        }
        //아이피가 없을 경우 새롭게 IP추가
        IpTrace newIp = new IpTrace();
        newIp.setIp(ip);
        newIp.setRequestCount(1);
        newIp.setLastRequestTime(currentTime);
        ipTraceRepository.save(newIp);
        
        log.info("SMS 요청 아이피 5회 이하 요청 검증 끝 - 새로운 아이피에서 요청이 들어옴");
        return true;
    }


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
