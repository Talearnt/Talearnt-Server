package com.talearnt.user.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import com.talearnt.admin.category.entity.QTalentCategory;
import com.talearnt.user.request.MyTalentsReqDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MyTalentQueryRepository {
    private final JPAQueryFactory factory;

    /** 등록하는 혹은 수정하는 모든 재능 키워드가 있는 지 확인, Exception 발생용 <br>
     * 조건 <br>
     * - TalentCategory Table에 존재하는가? <br>
     * 모든 키워드가 존재하지 않으면 True, 존재하면 False*/
    public Boolean validateIsCategory(List<MyTalentsReqDTO> talents){
        QTalentCategory talentCategory = QTalentCategory.talentCategory;
        return null;
    }
}
