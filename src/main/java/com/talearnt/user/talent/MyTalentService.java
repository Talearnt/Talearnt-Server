package com.talearnt.user.talent;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.enums.user.UserRole;
import com.talearnt.user.talent.entity.MyTalent;
import com.talearnt.user.talent.repository.MyTalentQueryRepository;
import com.talearnt.user.talent.repository.MyTalentRepository;
import com.talearnt.user.talent.request.MyTalentReqDTO;
import com.talearnt.user.talent.response.MyTalentsListResDTO;
import com.talearnt.user.talent.response.MyTalentsResDTO;
import com.talearnt.util.common.UserUtil;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.jwt.UserInfo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;


@Service
@Log4j2
@RequiredArgsConstructor
public class MyTalentService {

    //Repositories
    private final MyTalentQueryRepository myTalentQueryRepository;
    private final MyTalentRepository myTalentRepository;

    /** 나의 재능 추가하기<br>
     * 조건<br>
     * - 로그인이 되어 있을 것 (컨트롤러에서 확인)<br>
     * - 등록된 재능 코드일 것
     * @param talents 나의 재능 키워드들
     *                type : <br>
     *                false : 주고 싶은<br>
     *                true : 받고 싶은
     * */
    @Transactional
    public String addMyTalents(MyTalentReqDTO talents){
        log.info("나의 재능, 관심 있는 재능들 추가 시작 : {}",talents);

        //등록된 재능 코드인지 확인 : 모두 있을 경우 False, 없을 경우 True
        if(myTalentQueryRepository.validateIsCategory(talents.getGiveTalents())
        && myTalentQueryRepository.validateIsCategory(talents.getInterestTalents())) {
            log.error("나의 재능, 관심있는 재능들 추가 실패 - 존재하지 않은 키워드 : {}", ErrorCode.KEYWORD_CATEGORY_NOT_FOUND);
            throw new CustomRuntimeException(ErrorCode.KEYWORD_CATEGORY_NOT_FOUND);
        }

        //키워드 등록 전 변환
        List<MyTalent> giveTalents = MyTalentMapper.INSTANCE.toGiveEntities(talents.getGiveTalents(), talents.getUserInfo());
        List<MyTalent> interestTalents = MyTalentMapper.INSTANCE.toInterestEntities(talents.getInterestTalents(), talents.getUserInfo());
        
        //키워드 합치기
        giveTalents.addAll(interestTalents);

        
        //키워드 저장
        myTalentRepository.saveAll(giveTalents);
        log.info("나의 재능, 관심 있는 재능들 추가 끝");
        return "성공적으로 나의 재능 및 관심 재능 키워드가 등록되었습니다.";
    }

    /** 나의 재능 키워드 목록 보여주기 <br>
     * 조건<br>
     * - 로그인 되어 있을 것<br>
     * - 유저 이상의 권한을 가지고 있을 것 (Controller에서 확인)<br>
     * - 재능 키워드에서 활성화 된 것들만 제공<br>
     *
     * 추후 고려사항<br>
     * - 나의 재능에는 활성화 되어 있지만, 재능 키워드에서 활성화 되지 않은 것들은<br>
     *      DB에서 삭제
     * @param authentication
     * */
    public List<MyTalentsResDTO> getMyGiveTalents(Authentication authentication){
        log.info("나의 재능 키워드 목록 가져오기 시작 : {} ", authentication);
        //로그인 여부 확인
        UserInfo userInfo = UserUtil.validateAuthentication("나의 재능 키워드 목록 가져오기" , authentication);

        log.info("나의 재능 키워드 목록 가져오기 끝");
        return myTalentQueryRepository.getActivatedTalentsForMyTalents(userInfo.getUserNo());
    }
}
