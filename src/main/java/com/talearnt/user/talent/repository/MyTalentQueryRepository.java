package com.talearnt.user.talent.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import com.talearnt.admin.category.entity.QTalentCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MyTalentQueryRepository {
    private final JPAQueryFactory factory;

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
}
