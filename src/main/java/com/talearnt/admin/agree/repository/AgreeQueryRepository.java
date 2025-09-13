package com.talearnt.admin.agree.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import com.talearnt.admin.agree.entity.Agree;
import com.talearnt.admin.agree.entity.QAgree;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AgreeQueryRepository {

    private final JPAQueryFactory factory;
    private final QAgree agree = QAgree.agree1;

    public Optional<Agree> findByUserNoAndAgreeCode(Long userNo, Long agreeCodeNo) {
        Agree result = factory.selectFrom(agree)
                .where(agree.user.userNo.eq(userNo)
                        .and(agree.agreeCode.agreeCodeId.eq(agreeCodeNo)))
                .fetchOne();
        return Optional.ofNullable(result);
    }

}
