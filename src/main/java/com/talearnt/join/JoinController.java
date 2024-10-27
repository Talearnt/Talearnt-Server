package com.talearnt.join;

import com.talearnt.util.RestControllerV1;
import com.talearnt.verification.VerificationReqDTO;
import com.talearnt.verification.VerificationService;
import com.talearnt.verification.VerifyCodeReqDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestControllerV1
@RequiredArgsConstructor
public class JoinController {

    private final JoinService joinService;
    private final VerificationService verificationService;

    @PostMapping("/join")   // 회원 등록
    public ResponseEntity<String> addUser(@RequestBody JoinReqDTO joinReqDTO){

        try {
            if(verificationService.isVerifiedCheck(joinReqDTO)){
                joinService.registerUser(joinReqDTO);
                return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
            }else {
                return new ResponseEntity<>("User not verified", HttpStatus.BAD_REQUEST);
            }
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/sendSMS")    // 인증 문자메세지 발송
    public void sendSMS(@RequestBody VerificationReqDTO verificationReqDTO){
        String verificationCode = Integer.toString((int)(Math.random() * (9999 - 1000 + 1)) + 1000);
        verificationReqDTO.setVerificationCode(verificationCode);
        verificationService.sendVerificationMessage(verificationReqDTO);
    }

    @PostMapping("/verifyCode")    // User가 입력한 code 검증
    public boolean verifyCode(@RequestBody VerifyCodeReqDTO verifyCodeReqDTO){

        return verificationService.verifyCode(verifyCodeReqDTO);
    }
}
