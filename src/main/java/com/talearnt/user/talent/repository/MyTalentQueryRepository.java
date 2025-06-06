package com.talearnt.user.talent.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import com.talearnt.admin.category.entity.QTalentCategory;
import com.talearnt.user.talent.entity.MyTalent;
import com.talearnt.user.talent.entity.QMyTalent;
import com.talearnt.user.talent.response.MyTalentsResDTO;
import com.talearnt.user.talent.response.QMyTalentsResDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MyTalentQueryRepository {
    private final JPAQueryFactory factory;

    private final QMyTalent myTalent = QMyTalent.myTalent;
    private final QTalentCategory talentCategory = QTalentCategory.talentCategory;


    /** 받고 싶은 재능 코드 반환하는 메소드*/
    public List<Integer> getReceiveTalentCodesByUserNo(Long userNo){
        return factory.select(myTalent.talentCategory.talentCode)
                .from(myTalent)
                .where(
                        myTalent.isActive.eq(true),//나의 재능을 사용하고 있는지 판단.
                        myTalent.user.userNo.eq(userNo),// 유저 회원 번호와 같으며
                        myTalent.type.eq(true)//주고 싶은 재능일 것
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
