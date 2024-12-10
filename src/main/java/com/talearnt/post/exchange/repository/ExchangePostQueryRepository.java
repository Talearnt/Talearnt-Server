package com.talearnt.post.exchange.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import com.talearnt.admin.category.entity.QTalentCategory;
import com.talearnt.user.talent.entity.QMyTalent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ExchangePostQueryRepository {
    private final JPAQueryFactory factory;

    /**활성화된 나의 주고 싶은 재능들 가져오기*/
    public List<Integer> getWantGiveMyTalents(Long userNo){
        QMyTalent myTalent = QMyTalent.myTalent;
        QTalentCategory talentCategory = QTalentCategory.talentCategory;
        return factory
                .select(myTalent.talentCategory.talentCode)
                .from(myTalent)
                .innerJoin(talentCategory)
                .on(talentCategory.eq(myTalent.talentCategory))
                .where(myTalent.user.userNo.eq(userNo)//아이디가 같고
                        .and(myTalent.type.eq(false))//주고 싶은 재능중에
                        .and(myTalent.isActive.eq(true)))//활성화 되어 있는 것
                .fetch();
    }

}
