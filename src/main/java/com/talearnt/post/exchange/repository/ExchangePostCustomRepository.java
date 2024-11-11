package com.talearnt.post.exchange.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.talearnt.post.exchange.response.ExchangePostListResDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ExchangePostCustomRepository {
    private final JPAQueryFactory queryFactory;

    public List<ExchangePostListResDTO> findExchangePostListByFilter(){
        /* 앞으로 확인 할 부분
         * 1. List<ExchangePostListResDTO>로 반환이 되는가?
         * 2. 쿼리는 어떤 식으로 짜게 되는가?
         *      2-1. 조건이 제대로 들어가는가?
         * 3. 10000번 째 쿼리를 가져올 때 pagination 속도 개선 방향 No offset
         * */
        return null;
    }

}
