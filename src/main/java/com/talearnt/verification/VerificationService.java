package com.talearnt.verification;


import com.talearnt.join.JoinReqDTO;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
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
    private final  ModelMapper mapper;

    public VerificationService(@Value("${coolsms.apiKey}") String apiKey,
                               @Value("${coolsms.secretKey}") String apiSecret,
                               @Value("${coolsms.fromNumber}") String fromNumber,
                               VerificationCodeRepository verificationCodeRepository,
                               ModelMapper mapper) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.fromNumber = fromNumber;
        this.verificationCodeRepository = verificationCodeRepository;
        this.mapper = mapper;
        // 여기서 messageService 초기화
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    // coolsms 인증 문자 보내기
    public SingleMessageSentResponse sendVerificationMessage(@RequestBody VerificationReqDTO verificationReqDTO) {
        Message message = new Message();

        message.setFrom(fromNumber);
        message.setTo(verificationReqDTO.getPhone());
        message.setText("Talearnt 인증번호는 [" + verificationReqDTO.getVerificationCode() + "] 입니다.");
        verificationReqDTO.setIsPhoneVerified(false);
        PhoneVerification phoneVerification = mapper.map(verificationReqDTO,PhoneVerification.class);
        verificationCodeRepository.save(phoneVerification);

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        System.out.println(response);

        return response;
    }

    // phone_verification table의 code 검증
    public boolean verifyCode(@RequestBody VerifyCodeReqDTO verifyCodeReqDTO) {
        // DB에서 userId에 해당하는 VerificationCodeEntity를 가져옴
        PhoneVerification phoneVerification = verificationCodeRepository.findByUserId(verifyCodeReqDTO.getUserId());

        // 검증: 클라이언트가 입력한 코드와 DB의 코드 비교
        if (phoneVerification != null && phoneVerification.getVerificationCode().equals(verifyCodeReqDTO.getInputCode())) {
            // 인증 성공
            System.out.println("인증 성공");
            // 인증 성공하면 isPhoneVerified를 true로 변경
            phoneVerification.setIsPhoneVerified(true);
            verificationCodeRepository.save(phoneVerification);
            return true;
        } else {
            // 인증 실패
            System.out.println("인증 실패");
            return false;
        }
    }

    // 해당 userId에 본인 인증이 된 상태인지 check
    public Boolean isVerifiedCheck(@RequestBody JoinReqDTO joinReqDTO){
        PhoneVerification phoneVerification = verificationCodeRepository.findByUserId(joinReqDTO.getUserId());
        if(phoneVerification.getIsPhoneVerified()==true){
            return true;
        }else {
            return false;
        }

    }

}
