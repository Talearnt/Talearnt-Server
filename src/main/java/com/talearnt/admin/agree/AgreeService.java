package com.talearnt.admin.agree;

import com.talearnt.admin.agree.entity.Agree;
import com.talearnt.admin.agree.entity.AgreeCode;
import com.talearnt.admin.agree.entity.AgreeContent;
import com.talearnt.admin.agree.repository.*;
import com.talearnt.admin.agree.request.AgreeCodeReqDTO;
import com.talearnt.admin.agree.response.AgreeCodeListResDTO;
import com.talearnt.admin.agree.response.AgreeMarketingAndAdvertisingResDTO;
import com.talearnt.enums.common.ErrorCode;
import com.talearnt.user.infomation.entity.User;
import com.talearnt.util.common.UserUtil;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.filter.UserRequestLimiter;
import com.talearnt.util.jwt.UserInfo;
import com.talearnt.util.log.LogRunningTime;
import com.talearnt.util.response.CommonResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class AgreeService {

    private final UserRequestLimiter limiter;

    private final AgreeCodeRepository agreeCodeRepository;
    private final AgreeContentRepository agreeContentRepository;
    private final AgreeCodeCustomRepository agreeCodeCustomRepository;
    private final AgreeQueryRepository agreeQueryRepository;
    private final AgreeRepository agreeRepository;

    public ResponseEntity<CommonResponse<String>> addAgreeCodeAndAgreeContent(AgreeCodeReqDTO agreeCodeReqDTO){
        log.info("이용 약관 생성 시작 : {}",agreeCodeReqDTO);

        //DTO -> Entity로 변경 후 저장
        AgreeCode agreeCode = AgreeMapper.INSTANCE.toAgreeCodeEntity(agreeCodeReqDTO);
        agreeCode = agreeCodeRepository.save(agreeCode);
        
        //이용 약관 내용에 필요한 이용 약관 정보 셋팅
        AgreeContent agreeContent = AgreeMapper.INSTANCE.toAgreeContentEntity(agreeCodeReqDTO);
        agreeContent.setAgreeCode(agreeCode);
        
        //이용 약관 내용 저장
        agreeContentRepository.save(agreeContent);
        return CommonResponse.success("이용 약관 등록에 성공하였습니다.");
    }


    /** 활성화된 이용약관 가져오기*/
    public ResponseEntity<CommonResponse<List<AgreeCodeListResDTO>>> getActivatedAgreeCodeList(){
        return CommonResponse.success(agreeCodeCustomRepository.getActivatedAgreeCodeList());
    }

    public AgreeMarketingAndAdvertisingResDTO getAgreeMarketingAndAdvertising(Authentication authentication) {
        log.info("마케팅 이용 약관 및 광고성 정보 수신 동의 여부 조회 시작");

        UserInfo userInfo = UserUtil.validateAuthentication("마케팅 이용 약관 및 광고성 정보 수신 동의 여부 조회", authentication);

        //마케팅 이용 약관 동의 여부 조회
        Agree marketingAgree = agreeQueryRepository.findByUserNoAndAgreeCode(userInfo.getUserNo(), 3L)
                .orElse(new Agree(null,new AgreeCode(3L), new User(userInfo.getUserNo()),false, LocalDateTime.now(),LocalDateTime.now()));

        //최초 조회시 마케팅 이용 약관이 없을 경우 생성
        if (marketingAgree.getAgreeNo() == null){
            log.warn("마케팅 이용 약관 동의 여부 조회시 해당 유저의 마케팅 이용 약관이 존재하지 않아 최초 생성 진행 : 유저 번호 ({})",userInfo.getUserNo());
            agreeRepository.save(marketingAgree);
        }

        //광고성 정보 수신 동의 여부 조회
        Agree advertisingAgree = agreeQueryRepository.findByUserNoAndAgreeCode(userInfo.getUserNo(), 4L)
                .orElse(new Agree(null,new AgreeCode(4L), new User(userInfo.getUserNo()),false, LocalDateTime.now(),LocalDateTime.now()));

        //최초 조회시 광고성 정보 수신 동의 약관이 없을 경우 생성
        if (advertisingAgree.getAgreeNo() == null){
            log.warn("광고성 정보 수신 동의 여부 조회시 해당 유저의 광고성 정보 수신 동의 약관이 존재하지 않아 최초 생성 진행 : 유저 번호 ({})",userInfo.getUserNo());
            agreeRepository.save(advertisingAgree);
        }


        log.info("마케팅 이용 약관 및 광고성 정보 수신 동의 여부 조회 완료 : 유저 번호 ({}) - 마케팅 ({}) - 광고성 ({})",userInfo.getUserNo(), marketingAgree.isAgree(), advertisingAgree);
        return AgreeMarketingAndAdvertisingResDTO.builder()
                .isMarketing(marketingAgree.isAgree())
                .isAdvertising(advertisingAgree.isAgree())
                .build();

    }


    @Async
    @LogRunningTime
    @Transactional
    public void switchMarketingAgreeCode(Boolean isMarketingAgree, UserInfo userInfo){
        log.info("마케팅 이용 약관 동의 여부 변경 시작 : {} - {}", isMarketingAgree,userInfo);

        //요청 제한 체크
        if (!limiter.isAllowed(userInfo.getUserNo())){
            log.error("마케팅 이용 약관 동의 여부 변경 실패 - 요청 제한 초과 : {} - {}",userInfo.getUserNo(), ErrorCode.TOO_MANY_REQUESTS);
            throw new CustomRuntimeException(ErrorCode.TOO_MANY_REQUESTS);
        }

        //마케팅 이용 약관 조회
        Agree agree = agreeQueryRepository.findByUserNoAndAgreeCode(userInfo.getUserNo(), 3L)
                .orElse(new Agree(null,new AgreeCode(3L), new User(userInfo.getUserNo()),isMarketingAgree, LocalDateTime.now(),LocalDateTime.now()));

        //마케팅 이용 약관 동의 여부 변경
        agree.setAgree(isMarketingAgree);
        agree.setAgreeDate(LocalDateTime.now());

        //변경된 마케팅 이용 약관 저장
        Agree changedAgree = agreeRepository.save(agree);
        log.info("마케팅 이용 약관 동의 여부 변경 완료 : {}",changedAgree);
    }

    @Async
    @LogRunningTime
    @Transactional
    public void switchAdvertisingAgreeCode(Boolean isAdvertisingAgree, UserInfo userInfo){
        log.info("광고성 정보 수신 동의 여부 변경 시작 : {} - {}", isAdvertisingAgree,userInfo);

        //요청 제한 체크
        if (!limiter.isAllowed(userInfo.getUserNo())){
            log.error("광고성 정보 수신 동의 여부 변경 실패 - 요청 제한 초과 : {} - {}",userInfo.getUserNo(), ErrorCode.TOO_MANY_REQUESTS);
            throw new CustomRuntimeException(ErrorCode.TOO_MANY_REQUESTS);
        }

        //광고성 정보 수신 동의 약관 조회
        Agree agree = agreeQueryRepository.findByUserNoAndAgreeCode(userInfo.getUserNo(), 4L)
                .orElse(new Agree(null,new AgreeCode(4L), new User(userInfo.getUserNo()),isAdvertisingAgree, LocalDateTime.now(),LocalDateTime.now()));

        //광고성 정보 수신 동의 여부 변경
        agree.setAgree(isAdvertisingAgree);
        agree.setAgreeDate(LocalDateTime.now());

        //변경된 광고성 정보 수신 동의 약관 저장
        Agree changedAgree = agreeRepository.save(agree);
        log.info("광고성 정보 수신 동의 여부 변경 완료 : {}",changedAgree);
    }

}
