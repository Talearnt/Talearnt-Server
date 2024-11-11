package com.talearnt.admin.agree;

import com.talearnt.admin.agree.entity.AgreeCode;
import com.talearnt.admin.agree.entity.AgreeContent;
import com.talearnt.admin.agree.repository.AgreeCodeCustomRepository;
import com.talearnt.admin.agree.repository.AgreeCodeRepository;
import com.talearnt.admin.agree.repository.AgreeContentRepository;
import com.talearnt.admin.agree.request.AgreeCodeReqDTO;
import com.talearnt.admin.agree.response.AgreeCodeListResDTO;
import com.talearnt.util.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class AgreeService {

    private final AgreeCodeRepository agreeCodeRepository;
    private final AgreeContentRepository agreeContentRepository;
    private final AgreeCodeCustomRepository agreeCodeCustomRepository;

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

    public ResponseEntity<CommonResponse<List<AgreeCodeListResDTO>>> getAcivatedAgreeCodeList(){
        return CommonResponse.success(agreeCodeCustomRepository.getActivatedAgreeCodeList());
    }
}
