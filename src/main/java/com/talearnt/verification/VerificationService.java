package com.talearnt.verification;


import com.talearnt.enums.ErrorCode;
import com.talearnt.join.request.JoinReqDTO;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.response.CommonResponse;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class VerificationService {
    @Value("${coolsms.apiKey}")
    private String apiKey;

    @Value("${coolsms.secretKey}")
    private String apiSecret;

    @Value("${coolsms.fromNumber}")
    private String fromNumber;

    private final  DefaultMessageService messageService;
    private final  VerificationCodeRepository verificationCodeRepository;

    public VerificationService(@Value("${coolsms.apiKey}") String apiKey,
                               @Value("${coolsms.secretKey}") String apiSecret,
                               @Value("${coolsms.fromNumber}") String fromNumber,
                               VerificationCodeRepository verificationCodeRepository)
            {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.fromNumber = fromNumber;
        this.verificationCodeRepository = verificationCodeRepository;
        // 여기서 messageService 초기화
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    // coolsms 인증 문자 보내기
    public ResponseEntity<CommonResponse<String>> sendVerificationMessage(@RequestBody VerificationReqDTO verificationReqDTO) {
        //인증 코드 설정
        String verificationCode = Integer.toString((int)(Math.random() * (9999 - 1000 + 1)) + 1000);
        verificationReqDTO.setVerificationCode(verificationCode);

        Message message = new Message();

        message.setFrom(fromNumber);
        message.setTo(verificationReqDTO.getPhone());
        message.setText("Talearnt 인증번호는 [ " + verificationReqDTO.getVerificationCode() + " ] 입니다.");
        verificationReqDTO.setIsPhoneVerified(false);
        PhoneVerification phoneVerification = VerificationMapper.INSTANCE.toPhoneVerification(verificationReqDTO);
        verificationCodeRepository.save(phoneVerification);

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        System.out.println(response.getStatusMessage());

        return CommonResponse.success(response.getStatusMessage());
    }

    // phone_verification table의 code 검증
    public ResponseEntity<CommonResponse<Boolean>> verifyCode(@RequestBody VerifyCodeReqDTO verifyCodeReqDTO) {
        // DB에서 userId에 해당하는 VerificationCodeEntity를 가져옴
        PhoneVerification phoneVerification = verificationCodeRepository.findByUserId(verifyCodeReqDTO.getUserId());

        // 검증: 클라이언트가 입력한 코드와 DB의 코드 비교
        if (phoneVerification == null && !phoneVerification.getVerificationCode().equals(verifyCodeReqDTO.getInputCode())) {
            throw new CustomRuntimeException(ErrorCode.INVALID_AUTH_CODE);
        }
        //인증 성공하면 isPhoneVerified를 true로 변경
        phoneVerification.setIsPhoneVerified(true);
        verificationCodeRepository.save(phoneVerification);
        return CommonResponse.success(true);
    }

    // 해당 userId에 본인 인증이 된 상태인지 check
    public Boolean isVerifiedCheck(@RequestBody JoinReqDTO joinReqDTO){
        PhoneVerification phoneVerification = verificationCodeRepository.findByUserId(joinReqDTO.getUserId());
        return phoneVerification.getIsPhoneVerified();
    }

}
