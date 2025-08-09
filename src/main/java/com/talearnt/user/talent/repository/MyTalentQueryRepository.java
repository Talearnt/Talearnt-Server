package com.talearnt.user.talent.repository;


import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.talearnt.admin.category.entity.QTalentCategory;
import com.talearnt.post.exchange.response.WantedReceiveTalentsUserDTO;
import com.talearnt.user.talent.entity.MyTalent;
import com.talearnt.user.talent.entity.QMyTalent;
import com.talearnt.user.talent.response.MyTalentsResDTO;
import com.talearnt.user.talent.response.QMyTalentsResDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MyTalentQueryRepository {
    private final JPAQueryFactory factory;

    private final QMyTalent myTalent = QMyTalent.myTalent;
    private final QTalentCategory talentCategory = QTalentCategory.talentCategory;

    /**
     * 지정된 재능 코드 목록에 해당하는 '받고 싶은 재능'으로 등록한 사용자 정보를 조회합니다.
     *
     * <p>조회된 사용자 정보는 다음을 포함합니다:</p>
     * <ul>
     *   <li>사용자 번호(userNo)</li>
     *   <li>사용자 ID(userId)</li>
     *   <li>사용자가 등록한 '받고 싶은 재능' 코드 목록 (쉼표로 구분된 문자열)</li>
     * </ul>
     *
     * <p>조회 조건:</p>
     * <ul>
     *   <li>사용자의 재능이 활성화 상태여야 함(isActive가 true)</li>
     *   <li>'받고 싶은 재능'으로 등록된 항목이어야 함(type이 true)</li>
     *   <li>재능 코드가 파라미터로 전달된 코드 목록에 포함되어야 함</li>
     * </ul>
     *
     * <p>GROUP_CONCAT 함수를 사용하여 각 사용자별로 여러 개의 재능 코드를 쉼표로 구분된 하나의 문자열로 연결합니다.</p>
     *
     * @param talentCodes 조회 대상이 되는 재능 코드 집합
     * @return 조건에 맞는 사용자 정보와 받고 싶은 재능 코드가 담긴 DTO 객체 집합
     */
    public Set<WantedReceiveTalentsUserDTO> getWantedReceiveTalentsUserByTalentCodes(Long authorUserNo, List<Integer> talentCodes) {
        return new HashSet<>(factory
                .select(Projections.constructor(WantedReceiveTalentsUserDTO.class,
                        myTalent.user.userNo,
                        myTalent.user.userId,
                        Expressions.stringTemplate("GROUP_CONCAT({0})", myTalent.talentCategory.talentCode)))
                .from(myTalent)
                .where(
                        myTalent.user.userNo.ne(authorUserNo), // 글 작성자 제외
                        myTalent.isActive.eq(true), // 나의 재능이 활성화된 상태
                        myTalent.type.eq(true), // 받고 싶은 재능
                        myTalent.talentCategory.talentCode.in(talentCodes) // 재능 코드가 일치하는 경우
                )
                .fetch());
    }


    /** 받고 싶은 재능 코드 반환하는 메소드*/
    public List<Integer> getReceiveTalentCodesByUserNo(Long userNo){
        return factory.select(myTalent.talentCategory.talentCode)
                .from(myTalent)
                .where(
                        myTalent.isActive.eq(true),//나의 재능을 사용하고 있는지 판단.
                        myTalent.user.userNo.eq(userNo),// 유저 회원 번호와 같으며
                        myTalent.type.eq(true)//받고 싶은 재능일 것
                ).fetch();
    }

    /** 주고 싶은 재능 코드 반환하는 메소드*/
    public List<Integer> getGiveTalentCodesByUserNo(Long userNo){
        return factory.select(myTalent.talentCategory.talentCode)
                .from(myTalent)
                .where(
                        myTalent.isActive.eq(true),//나의 재능을 사용하고 있는지 판단.
                        myTalent.user.userNo.eq(userNo),// 유저 회원 번호와 같으며
                        myTalent.type.eq(false)//주고 싶은 재능일 것
                ).fetch();
    }


    /** 등록하는 혹은 수정하는 모든 재능 키워드가 있는 지 확인, Exception 발생용 <br>
     * 조건 <br>
     * - TalentCategory Table에 isActive로 존재하는가? <br>
     * 모든 키워드가 존재하지 않으면 True, 존재하면 False*/
    public Boolean validateIsCategory(List<Integer> codes){

        long rightCodes = Optional.ofNullable(
                factory
                        .select(talentCategory.talentCode.count())
                        .from(talentCategory)
                        .where(
                                talentCategory.talentCode.in(codes)
                                        .and(talentCategory.isActive.eq(true))
                        )
                        .fetchOne()
        ).orElse(0L);

        return codes.size() != rightCodes;
    }

    /** 업데이트 시 사용 안하는 키워드가 있을 경우를 대비<br>
     *  유저의 나의 재능 중에 재능 키워드에서 활성화 된 것들만 가져옴.
     * */
    public List<MyTalentsResDTO> getActivatedTalentsForMyTalents(Long userNo){
        return factory
                .select(new QMyTalentsResDTO(talentCategory.talentCode, talentCategory.talentName))
                .from(myTalent)
                .innerJoin(talentCategory)
                .on(talentCategory.eq(myTalent.talentCategory)
                        .and(talentCategory.isActive.eq(true))) // 활성화된 키워드
                .where(myTalent.user.userNo.eq(userNo)
                        .and(myTalent.isActive.eq(true)) // 나의 재능이 활성화된 상태
                        .and(myTalent.type.eq(false))) // 주고 싶은 재능
                .fetch();
    }

    /** 나의 재능 키워드를 모두 가져오기
     * 비활성화된 것들을 활성화 시켜야 할 수 있으므로 비활성화된 것들도 가져온다.*/
    public List<MyTalent> getAllMyTalents(Long userNo){
        return factory
                .selectFrom(myTalent)
                .join(myTalent.talentCategory, talentCategory).fetchJoin()
                .where(myTalent.user.userNo.eq(userNo))
                .fetch();
    }

}
