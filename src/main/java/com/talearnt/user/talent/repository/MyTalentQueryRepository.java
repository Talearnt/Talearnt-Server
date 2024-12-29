package com.talearnt.user.talent.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import com.talearnt.admin.category.entity.QTalentCategory;
import com.talearnt.user.talent.entity.QMyTalent;
import com.talearnt.user.talent.response.MyTalentsResDTO;
import com.talearnt.user.talent.response.QMyTalentsResDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import static com.talearnt.user.talent.entity.QMyTalent.myTalent;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MyTalentQueryRepository {
    private final JPAQueryFactory factory;



    /** 나의재능 키워드가 설정되어 있는지 확인하는 메소드
     * return boolean isKeywordSet*/
    public Boolean isKeywordSetByUserNo(Long userNo){
        return factory.selectFrom(myTalent)
                .where(
                        myTalent.isActive.eq(true)//나의 재능을 사용하고 있는지 판단.
                                .and(
                                        myTalent.user.userNo.eq(userNo) //회원 번호가 가지고 있는
                                )
                ).fetch().size() >= 2; //주고싶은 재능 1개, 받고 싶은 재능 1개가 필수이므로 그 이하일 경우 False, 이상일 경우 True
    }


    /** 등록하는 혹은 수정하는 모든 재능 키워드가 있는 지 확인, Exception 발생용 <br>
     * 조건 <br>
     * - TalentCategory Table에 isActive로 존재하는가? <br>
     * 모든 키워드가 존재하지 않으면 True, 존재하면 False*/
    public Boolean validateIsCategory(List<Integer> codes){
        QTalentCategory talentCategory = QTalentCategory.talentCategory;

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
        QMyTalent myTalent = QMyTalent.myTalent;
        QTalentCategory talentCategory = QTalentCategory.talentCategory;
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

}
