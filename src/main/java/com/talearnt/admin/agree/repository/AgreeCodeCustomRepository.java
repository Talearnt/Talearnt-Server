package com.talearnt.admin.agree.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import static com.talearnt.admin.agree.entity.QAgreeCode.agreeCode;
import static com.talearnt.admin.agree.entity.QAgreeContent.agreeContent;
import com.talearnt.admin.agree.response.AgreeCodeListResDTO;
import com.talearnt.admin.agree.response.QAgreeCodeListResDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AgreeCodeCustomRepository {
    private final JPAQueryFactory queryFactory;

    public List<AgreeCodeListResDTO> getActivatedAgreeCodeList(){
        return queryFactory.select(new QAgreeCodeListResDTO(
                agreeCode.agreeCodeId,
                agreeCode.title,
                agreeCode.version,
                agreeCode.mandatory,
                agreeContent.agreeContentId,
                agreeContent.content
        ))
                .from(agreeCode)
                .join(agreeContent)
                .on(agreeCode.agreeCodeId.eq(agreeContent.agreeCode.agreeCodeId))
                .where(agreeCode.active.eq(true))
                .fetch();
    }

    private BooleanExpression isActiveEq(boolean active){
        return null;
    }

}
