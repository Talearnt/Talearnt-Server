package com.talearnt.user;

import com.talearnt.enums.ErrorCode;
import com.talearnt.user.entity.User;
import com.talearnt.user.reponse.UserFindIdResDTO;
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

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserService {


    private final PasswordEncoder passwordEncoder;

    //Repositories
    private final UserRepository userRepository;

    public ResponseEntity<CommonResponse<String>> changeTestPwd(TestChangePwdReqDTO testChangePwdReqDTO){
        User user = userRepository.findByUserId(testChangePwdReqDTO.getUserId())
                .orElseThrow(() -> new CustomRuntimeException(ErrorCode.USER_NOT_FOUND));

        user.setPw(passwordEncoder.encode(testChangePwdReqDTO.getPw()));

        userRepository.save(user);
        return CommonResponse.success("테스트 비밀번호 변경 성공");
    }



}
