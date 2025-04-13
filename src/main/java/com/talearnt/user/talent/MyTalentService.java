package com.talearnt.user.talent;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.user.talent.entity.MyTalent;
import com.talearnt.user.talent.repository.MyTalentQueryRepository;
import com.talearnt.user.talent.repository.MyTalentRepository;
import com.talearnt.user.talent.request.MyTalentReqDTO;
import com.talearnt.user.talent.response.MyTalentsResDTO;
import com.talearnt.util.common.UserUtil;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.jwt.UserInfo;
import com.talearnt.util.log.LogRunningTime;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Log4j2
@RequiredArgsConstructor
public class MyTalentService {

    //Repositories
    private final MyTalentQueryRepository myTalentQueryRepository;
    private final MyTalentRepository myTalentRepository;
    private final JdbcTemplate jdbcTemplate;

    /**
     * 나의 재능 추가하기<br>
     * 조건<br>
     * - 로그인이 되어 있을 것 (컨트롤러에서 확인)<br>
     * - 등록된 재능 코드일
     * - 소프트 삭제된 코드가 있는 지 확인(소프트 삭제 됐다면 활성화로 변경 - 추후 개선점)
     *
     * @param talents 나의 재능 키워드들
     *                type : <br>
     *                false : 주고 싶은<br>
     *                true : 받고 싶은
     */
    @Transactional
    public String addMyTalents(MyTalentReqDTO talents) {
        log.info("나의 재능, 관심 있는 재능들 추가 시작 : {}", talents);

        //등록된 재능 코드인지 확인 : 모두 있을 경우 False, 없을 경우 True
        if (myTalentQueryRepository.validateIsCategory(talents.getGiveTalents())
                && myTalentQueryRepository.validateIsCategory(talents.getReceiveTalents())) {
            log.error("나의 재능, 관심있는 재능들 추가 실패 - 존재하지 않은 키워드 : {}", ErrorCode.KEYWORD_CATEGORY_NOT_FOUND);
            throw new CustomRuntimeException(ErrorCode.KEYWORD_CATEGORY_NOT_FOUND);
        }

        //키워드 등록 전 변환
        List<MyTalent> giveTalents = MyTalentMapper.INSTANCE.toGiveEntities(talents.getGiveTalents(), talents.getUserInfo());
        List<MyTalent> interestTalents = MyTalentMapper.INSTANCE.toInterestEntities(talents.getReceiveTalents(), talents.getUserInfo());

        //키워드 합치기
        giveTalents.addAll(interestTalents);


        //키워드 저장
        myTalentRepository.saveAll(giveTalents);
        log.info("나의 재능, 관심 있는 재능들 추가 끝");
        return "성공적으로 나의 재능 및 관심 재능 키워드가 등록되었습니다.";
    }

    /**
     * 나의 재능 키워드 목록 보여주기 <br>
     * 조건<br>
     * - 로그인 되어 있을 것<br>
     * - 유저 이상의 권한을 가지고 있을 것 (Controller에서 확인)<br>
     * - 재능 키워드에서 활성화 된 것들만 제공<br>
     * <p>
     * 추후 고려사항<br>
     * - 나의 재능에는 활성화 되어 있지만, 재능 키워드에서 활성화 되지 않은 것들은<br>
     * DB에서 삭제
     *
     * @param authentication 회원정보 추출용
     */
    public List<MyTalentsResDTO> getMyGiveTalents(Authentication authentication) {
        log.info("나의 재능 키워드 목록 가져오기 시작 : {} ", authentication);
        //로그인 여부 확인
        UserInfo userInfo = UserUtil.validateAuthentication("나의 재능 키워드 목록 가져오기", authentication);

        log.info("나의 재능 키워드 목록 가져오기 끝");
        return myTalentQueryRepository.getActivatedTalentsForMyTalents(userInfo.getUserNo());
    }


    /**
     * 나의 재능 키워드 수정하기<br>
     * 조건<br>
     * - 로그인 되어 있을 것<br>
     * - 수정할 키워드가 Talent 키워드가 존재하는지 확인<br>
     * - Talent 키워드가 존재하지 않는다면, DB에 저장<br>
     * - 비활성화일 경우에 활성화<br>
     * - 활성화된 키워드와 수정할 Talent 키워드에 없으면 비활성화 처리<br>
     *
     * @param userInfo       userInfo
     * @param giveTalents    수정할 주고 싶은 재능 키워드 코드들
     * @param receiveTalents 수정할 받고 싶은 재능 키워드 코드들
     */
    @Transactional
    @LogRunningTime
    public void updateMyTalents(UserInfo userInfo, List<Integer> giveTalents, List<Integer> receiveTalents) {
        log.info("나의 재능 키워드 수정 시작 - 주고 싶은 : {}, 받고 싶은 : {}", giveTalents, receiveTalents);

        //Mytalent 테이블에서 유저 번호로 나의 재능 키워드들 가져오기
        List<MyTalent> myTalents = myTalentQueryRepository.getAllMyTalents(userInfo.getUserNo());

        /*새롭게 추가할 주고 싶은 키워드들
         * 추가된 적이 있는지 판단*/
        List<Integer> willAddGiveTalentCodes = giveTalents.stream()
                .filter(code -> myTalents.stream()
                        .noneMatch(myTalent -> !myTalent.getType() && myTalent.getTalentCategory().getTalentCode().equals(code)))
                .toList();

        /* 새롭게 추가할 받고 싶은 키워드들
         * 추가된 적이 있는지 판단
         * */
        List<Integer> willAddReceiveTalentCodes = receiveTalents.stream()
                .filter(code -> myTalents.stream()
                        .noneMatch(myTalent -> myTalent.getType() && myTalent.getTalentCategory().getTalentCode().equals(code)))
                .toList();

        /* 비활성화할 주고 싶은 키워드들
         * 활성화된 것들 중에서 수정할 키워드에 없는 것들은 비활성화*/
        List<MyTalent> willDeactivateGiveTalents = myTalents.stream()
                .filter(myTalent -> myTalent.getIsActive() && !myTalent.getType() && !giveTalents.contains(myTalent.getTalentCategory().getTalentCode()))
                .peek(myTalent -> myTalent.setIsActive(false))
                .toList();


        /* 비활성화할 받고 싶은 키워드들
         * 활성화된 것들 중에서 수정할 키워드에 없는 것들은 비활성화*/
        List<MyTalent> willDeactivateReceiveTalents = myTalents.stream()
                .filter(myTalent -> myTalent.getIsActive() && myTalent.getType() && !receiveTalents.contains(myTalent.getTalentCategory().getTalentCode()))
                .peek(myTalent -> myTalent.setIsActive(false))
                .toList();

        /* 활성화할 주고 싶은 키워드들
         * 비활성화 된 것들 중에서 수정할 키워드에 있는 것은 활성화*/
        List<MyTalent> willActivateGiveTalents = myTalents.stream()
                .filter(myTalent -> !myTalent.getIsActive() && !myTalent.getType() && giveTalents.contains(myTalent.getTalentCategory().getTalentCode()))
                .peek(myTalent -> myTalent.setIsActive(true))
                .toList();

        /* 활성화할 받고 싶은 키워드들
         * 비활성화 된 것들 중에서 수정할 키워드에 있는 것은 활성화*/
        List<MyTalent> willActivateReceiveTalents = myTalents.stream()
                .filter(myTalent -> !myTalent.getIsActive() && myTalent.getType() && receiveTalents.contains(myTalent.getTalentCategory().getTalentCode()))
                .peek(myTalent -> myTalent.setIsActive(true))
                .toList();

        //추가할 키워드 변환
        List<MyTalent> addTalents = MyTalentMapper.INSTANCE.toGiveEntities(willAddGiveTalentCodes, userInfo);
        List<MyTalent> interestTalents = MyTalentMapper.INSTANCE.toInterestEntities(willAddReceiveTalentCodes, userInfo);

        //추가할 키워드 리스트 병합
        addTalents.addAll(interestTalents);

        //insert 쿼리
        String insertSQL = "INSERT INTO my_talent (user_no, talent_code, type) VALUES (?, ?, ?)";

        //벌크 인서트
        jdbcTemplate.batchUpdate(insertSQL, addTalents, 10,
                (ps, myTalent) -> {
                    ps.setLong(1, userInfo.getUserNo());
                    ps.setInt(2, myTalent.getTalentCategory().getTalentCode());
                    ps.setBoolean(3, myTalent.getType());
                });

        log.info("나의 재능 키워드 수정 끝");
    }
}
