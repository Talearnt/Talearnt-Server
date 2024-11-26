package com.talearnt.user;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.service.common.MailService;
import com.talearnt.user.entity.FindPasswrodUrl;
import com.talearnt.user.entity.User;
import com.talearnt.user.query.UserFindQueryDTO;
import com.talearnt.user.reponse.UserFindResDTO;
import com.talearnt.user.repository.FindPasswordUrlRepository;
import com.talearnt.user.repository.UserQueryRepository;
import com.talearnt.user.repository.UserRepository;
import com.talearnt.user.request.CheckUserPwdReqDTO;
import com.talearnt.user.request.CheckUserVerificationCodeReqDTO;
import com.talearnt.util.common.MailUtil;
import com.talearnt.util.common.UserUtil;
import com.talearnt.util.common.VerificationUtil;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.verification.Entity.PhoneVerification;
import com.talearnt.verification.Entity.PhoneVerificationTrace;
import com.talearnt.verification.VerificationMapper;
import com.talearnt.verification.repository.PhoneVerificationTraceRepository;
import com.talearnt.verification.repository.VerificationCodeQueryRepository;
import com.talearnt.verification.repository.VerificationCodeRepository;
import jakarta.mail.MessagingException;
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

import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserFindService {
    /**개선 할 부분, Verification Service에서도 동일한 코드가 발견되고 있음.*/
    @Value("${coolsms.fromNumber}")
    private String fromNumber;
    private final PasswordEncoder passwordEncoder;
    //메세지
    private final DefaultMessageService messageService;
    private final MailService mailService;

    //Repositories
    private final UserRepository userRepository;
    private final UserQueryRepository userQueryRepository;
    private final FindPasswordUrlRepository findPasswordUrlRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final VerificationCodeQueryRepository verificationCodeQueryRepository;
    private final PhoneVerificationTraceRepository phoneVerificationTraceRepository;

    /**아이디 찾기 인증 번호 발송<br>
     * 조건<br>
     * - 휴대폰 번호에 해당하는 유저가 있는가?<br>
     * - 정지 회원은 아닌가?<br>
     * - 휴대폰 문자 메세지 전송이 성공적으로 이루어졌는가?<br>
     * @param phoneNumber 회원의 휴대폰 번호
     * */
    public ResponseEntity<CommonResponse<String>> sendAuthenticationCode(String phoneNumber){
        log.info("유저 아이디 찾기 문자 전송 시작 : {}",phoneNumber);
        //유저의 아이디가 없기 때문에, 휴대폰 번호에 해당하는 유저를 가져오는 쿼리.
        UserFindQueryDTO userQueryDTO = userQueryRepository.selectUserByPhoneNumber(phoneNumber)
                .orElseThrow(()->new CustomRuntimeException(ErrorCode.USER_NOT_FOUND_PHONE_NUMBER));

        //정지된 회원이나, 탈퇴한 회원일 경우 Exception
        UserUtil.validateUserAuthority(userQueryDTO.getAuthority().name());

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
        //phoneVerification.setUserId(userQueryDTO.getUserId());

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        log.info("휴대폰 문자 전송 후 데이터 : {}",response);

        //성공적으로 문자 전송 시에 인증 번호 데이터 베이스에 저장.
        if (response!=null && response.getStatusCode().equals("2000")){
            verificationCodeRepository.save(phoneVerification);
        }

        log.info("유저 아이디 찾기 문자 전송 끝");
        return CommonResponse.success(response.getStatusMessage());
    }


    /**인증 번호 검증하는 단계.<br>
     * 이곳에서 DB가 검증 컬럼의 값이 True로 바뀔 경우<br>
     * 다른 곳에서 확인하여 작업할 수 있음.<br>
     * 조건 :<br>
     * 1. 10분 이내로 해당 휴대폰 번호로 만들어진 VerificationCode가 있는가?<br>
     * 2. 있다면, 시도는 총 몇번을 했나?<br>
     * 3. 5번 이상 했다면 해당 VerificationCode를 지우고 "10분 후 다시 시도 메세지 전송"
     * @param reqDTO 휴대폰 번호, 인증 코드
     * @return boolean
     * */
    public ResponseEntity<CommonResponse<Boolean>> checkVerificationCode(CheckUserVerificationCodeReqDTO reqDTO){
        log.info("유저 아이디 찾기 인증 번호 검증 시작 : {}",reqDTO);
        //10분 이내로 해당 번호로 발송된 인증 번호 가져오기.
        PhoneVerification phoneVerification = verificationCodeQueryRepository.selectPhoneVerificationByMinutesAndPhone(reqDTO.getPhone())
                .orElseThrow(()->{
                    log.error(ErrorCode.AUTH_NOT_FOUND_PHONE_CODE);
                    return new CustomRuntimeException(ErrorCode.AUTH_NOT_FOUND_PHONE_CODE);
                });
        //인증 번호 시도 횟수가 5번 이상일 경우 차단
        if (5 <= verificationCodeQueryRepository.countTryPhoneVerification(phoneVerification.getPhoneVerificationNo(),reqDTO.getPhone())){
            log.error("유저 찾기 아이디 인증 번호 검증 실패 - 5회 이상 시도 : {}",ErrorCode.AUTH_TOO_MANY_REQUEST);
            throw new CustomRuntimeException(ErrorCode.AUTH_TOO_MANY_REQUEST);
        }

        //인증 번호가 동일하지 않다면, PhoneVerificationTrace 테이블에 추가.
        if (!reqDTO.getCode().equals(phoneVerification.getVerificationCode())){
            PhoneVerificationTrace trace = VerificationMapper.INSTANCE.toTraceEntity(phoneVerification,reqDTO);
            phoneVerificationTraceRepository.save(trace);
            log.error("유저 찾기 아이디 인증 번호 검증 실패 - 인증 번호 같지 않음 : {}",trace);
            throw new CustomRuntimeException(ErrorCode.INVALID_AUTH_CODE);
        }

        //성공, 해당 코드 isPhoneVeified를 True 변환 후 저장 및 Trace 로그 삭제.
        phoneVerification.setIsPhoneVerified(true);
        verificationCodeRepository.save(phoneVerification);

        log.info("유저 아이디 찾기 인증 번호 검증 끝");
        return CommonResponse.success(true);
    }

    /** 유저가 인증을 마치고 완료 버튼을 눌렀을 때,<br>
     * 해당 유저가 보낸 인증 번호와 전화번호로 인증 완료를 확인한다.<br>
     * 조건<br>
     * - 10분 이내의 코드인가?<br>
     * - 해당 전화번호와 인증코드로 나온 코드 중에 활성화 된 것이 있는가?<br>
     * - 인증 완료를 하였는가?<br>
     * - 메세지 전송이 성공적으로 이루어졌는가?<br>
     * @param checkUserVerificationCodeReqDTO 휴대폰 번호, 인증 코드
     * @return userFindResDTO
     * */
    public ResponseEntity<CommonResponse<UserFindResDTO>> findUserIds(CheckUserVerificationCodeReqDTO checkUserVerificationCodeReqDTO){
        log.info("유저 아이디 찾기 완료 시작 {}",checkUserVerificationCodeReqDTO);
        // 10분 이내로 해당 전화번호와 코드로 이루어진 검증 코드를 인증 완료했는지 확인
        Boolean isVerifiedCode = verificationCodeQueryRepository.checkIsPhoneVerified(checkUserVerificationCodeReqDTO.getPhone(), checkUserVerificationCodeReqDTO.getCode())
                .orElseThrow(()-> {
                    log.error("유저 아이디 찾기 실패 - 10분 이내로 검증을 완료하지 않음 : {}",ErrorCode.AUTH_NOT_FOUND_PHONE_CODE);
                    return new CustomRuntimeException(ErrorCode.AUTH_NOT_FOUND_PHONE_CODE);
                });

        //아직 인증 완료를 하지 않았을 경우
        if (!isVerifiedCode){
            log.error("유저 아이디 찾기 실패 - 인증 완료를 하지 않은 코드 : {}",ErrorCode.UNVERIFIED_AUTH_CODE);
            throw new CustomRuntimeException(ErrorCode.UNVERIFIED_AUTH_CODE);
        }

        // 모든 조건 성립, 해당 유저의 아이디를 가져옴
        UserFindResDTO userFindResDTO = userQueryRepository.findUserIdAndCreatedAt(checkUserVerificationCodeReqDTO.getPhone())
                .orElseThrow(()->{
                    log.error("유저 아이디 찾기 실패 - 해당 휴대폰 번호로 가입한 회원이 없음 : {}",ErrorCode.USER_NOT_FOUND_PHONE_NUMBER);
                    return new CustomRuntimeException(ErrorCode.USER_NOT_FOUND_PHONE_NUMBER);
                });

        //메세지 설정
        Message message = new Message();
        message.setFrom(fromNumber);
        message.setTo(checkUserVerificationCodeReqDTO.getPhone());
        message.setText("Talearnt 회원님의 아이디는 [ " + userFindResDTO.getUserId() + " ] 입니다.");

        //메세지 전송
        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));

        //메세지 전송 실패
        if (response ==null || !response.getStatusCode().equals("2000")){
            log.error("유저 아이디 찾기 실패 - 메세지 전송 실패 : {}",ErrorCode.MESSAGE_NOT_RESPONSE);
            throw new CustomRuntimeException(ErrorCode.MESSAGE_NOT_RESPONSE);
        }

        return CommonResponse.success(userFindResDTO);
    }

    /**비밀번호를 변경을 위한 이메일을 전송한다.<br>
     * 전송할 수 있는 조건은 아래와 같다.<br>
     * 조건<br>
     * - 이메일 형식으로 제대로 넘어왔는가? (컨트롤러에서 확인)<br>
     * - 해당 유저가 존재하는가?<br>
     * - 해당 유저는 정지 혹은 탈퇴가 아닌가?<br>
     * - URL을 만들어 이메일로 전송을 성공했는가?<br>
     * @param userId 회원 가입된 유저 아이디
     * @return userFindResDTO
     * */
    @Transactional
    public ResponseEntity<CommonResponse<UserFindResDTO>> sendEmailForPwd(String userId) throws MessagingException {
        log.info("비밀번호 찾기 시작 : {}",userId);
        //아이디가 존재하는 지 확인
        UserFindQueryDTO checkAuth = userQueryRepository.findUserIdAndAuthorityByUserId(userId)
                .orElseThrow(()->{
                    log.error("비밀 번호 찾기 이메일 전송 실패 - 해당 회원을 찾을 수 없음 : {}",ErrorCode.USER_NOT_FOUND);
                    return new CustomRuntimeException(ErrorCode.USER_NOT_FOUND);
                });

        // 해당 아이디가 탈퇴인지, 정지인지 확인.
        UserUtil.validateUserAuthority(checkAuth.getAuthority().name());

        //UUID 생성
        String uuid = UUID.randomUUID().toString();

        //데이터 베이스에 저장
        FindPasswrodUrl pwdUrl = UserMapper.INSTANCE.toFindPasswordUrlEntity(checkAuth.getUserId(), uuid);
        //저장한 후 값 추출
        pwdUrl = findPasswordUrlRepository.save(pwdUrl);

        //메일 전송
        mailService.sendFindPasswordHtmlMessage(checkAuth.getUserId(),"[Talearnt] 비밀번호 재설정 인증 안내", MailUtil.MakeFindPasswrodMailForm(pwdUrl.getNo(), uuid));

        log.info("비밀번호 찾기 끝");
        return CommonResponse.success(UserMapper.INSTANCE.toUserFindResDTO(pwdUrl));
    }

    /**비밀번호 변경하는 페이지에서 넘어온 값들로 비밀번호를 변경한다.<br>
     * 조건<br>
     * - 패스워드 패턴이 일치하는가? (컨트롤러에서 체크)<br>
     * - 패스워드 두 값이 동일한가?<br>
     * - 10분내로 전송된 FindPasswrodUrl의 No와 UUID 값이 있는가?<br>
     * - 해당 유저가 존재하는가?
     * @param findPwdUrlNo FindPasswordUrl의 No
     * @param uuid FindPasswordUrl 의 고유 값
     * @param checkUserPwdReqDTO 변경할 비밀번호.
     * @return 성공적으로 비밀번호가 변경되었습니다.
     * */
    public ResponseEntity<CommonResponse<String>> changeUserPassword(Long findPwdUrlNo, String uuid, CheckUserPwdReqDTO checkUserPwdReqDTO){
        log.info("비밀번호 변경 시작 : {},{},{}",findPwdUrlNo,uuid,checkUserPwdReqDTO);

        //두 패스워드가 일치하는지 확인
        if(!checkUserPwdReqDTO.getPw().equals(checkUserPwdReqDTO.getCheckedPw())){
            log.error("비밀번호 변경 실패 - 두 개의 비밀번호가 일치하지 않음 : {}",ErrorCode.USER_PASSWROD_FAILED_DOUBLE_CHECK);
            throw new CustomRuntimeException(ErrorCode.USER_PASSWROD_FAILED_DOUBLE_CHECK);
        }

        //findPwdUrlNo 와 uuid 가 일치하는 Row에서 userId 가져오기
        String userId = userQueryRepository.findUserIdByUrlNoAndUuid(findPwdUrlNo,uuid)
                .orElseThrow(()->{
                    log.error("비밀번호 변경 실패 - 10분 이내로 등록된 No와 UUID가 존재하지 않음 : {}",ErrorCode.AUTH_NOT_FOUND_EMAIL_USER);
                    return new CustomRuntimeException(ErrorCode.AUTH_NOT_FOUND_EMAIL_USER);
                });

        //해당 유저가 존재하는 지 확인하는 쿼리.
        User user = userQueryRepository.findUserByUserId(userId)
                .orElseThrow(()->{
                    log.error("비밀번호 변경 실패 - 회원이 존재하지 않음 : {}",ErrorCode.USER_NOT_FOUND);
                    return new CustomRuntimeException(ErrorCode.USER_NOT_FOUND);
                });

        //해당 유저 비밀번호 암호화 및 저장
        user.setPw(passwordEncoder.encode(checkUserPwdReqDTO.getPw()));
        userRepository.save(user);

        //10분 내 무한으로 변경 불가능하도록 삭제
        findPasswordUrlRepository.deleteById(findPwdUrlNo);

        log.info("비밀번호 변경 끝");
        return CommonResponse.success("성공적으로 비밀번호가 변경되었습니다.");
    }


}
