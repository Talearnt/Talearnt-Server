package com.talearnt.post.exchange.repository;


import com.querydsl.core.Tuple;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.talearnt.admin.category.entity.QTalentCategory;
import com.talearnt.post.exchange.entity.QExchangePost;
import com.talearnt.post.exchange.entity.QFavoriteExchangePost;
import com.talearnt.post.exchange.entity.QGiveTalent;
import com.talearnt.post.exchange.entity.QReceiveTalent;
import com.talearnt.post.exchange.response.ExchangePostListResDTO;
import com.talearnt.post.exchange.response.TestListDTO;
import com.talearnt.user.infomation.entity.QUser;
import com.talearnt.user.talent.entity.QMyTalent;
import com.talearnt.util.common.PostUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.sql.ast.tree.expression.SqlSelectionExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Log4j2
public class ExchangePostQueryRepository {
    private final JPAQueryFactory factory;

    //QClasses
    private final QExchangePost exchangePost = QExchangePost.exchangePost;
    private final QUser user = QUser.user;
    private final QGiveTalent giveTalent = QGiveTalent.giveTalent;
    private final QReceiveTalent receiveTalent = QReceiveTalent.receiveTalent;
    private final QFavoriteExchangePost favoriteExchangePost = QFavoriteExchangePost.favoriteExchangePost;


    //test Query
    public Page<TestListDTO> getTest(){
        QTalentCategory giveTalentCategory = new QTalentCategory("giveTalentCategory");
        QTalentCategory receiveTalentCategory = new QTalentCategory("receiveTalentCategory");

        List<TestListDTO> res = factory
                .from(exchangePost)
                .leftJoin(giveTalent).on(giveTalent.exchangePost.exchangePostNo.eq(exchangePost.exchangePostNo))
                .leftJoin(receiveTalent).on(receiveTalent.exchangePost.exchangePostNo.eq(exchangePost.exchangePostNo))
                .distinct()
                .transform(GroupBy.groupBy(exchangePost.exchangePostNo).list(
                        Projections.constructor(TestListDTO.class,
                                exchangePost.exchangePostNo,
                                GroupBy.list( Expressions.stringTemplate("GROUP_CONCAT(DISTINCT {0})", JPAExpressions.select(giveTalentCategory.talentName).distinct()
                                        .from(giveTalentCategory)
                                        .where(giveTalentCategory.talentCode.eq(giveTalent.talentCode.talentCode)))),
                                GroupBy.list( GroupBy.list(JPAExpressions.select(receiveTalentCategory.talentName).distinct()
                                        .from(receiveTalentCategory)
                                        .where(receiveTalentCategory.talentCode.eq(receiveTalent.talentCode.talentCode))))
                )));
        log.info("res{} ", res);

        return null;
    }


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


    /**재능 교환 목록 불러오기 (Filter 조건)<br>*/
    public Page<ExchangePostListResDTO> getFilteredExchangePostList(List<String> categories,
                                                                    List<String> talents,
                                                                    String order,
                                                                    String duration,
                                                                    String type,
                                                                    String requiredBadge,
                                                                    String status,
                                                                    Pageable page){

//        List<ExchangePostListResDTO> result = factory.select(
//                new QExchangePostListResDTO(user.profileImg,
//                        user.nickname,
//                        user.authority,
//                        exchangePost.status,
//                        exchangePost.exchangeType,
//                        exchangePost.duration,
//                        exchangePost.requiredBadge,
//                        exchangePost.title,
//                        exchangePost.content,
//                        Expressions.
//                        JPAExpressions.select(giveTalent.talentCode.talentName)
//                                .from(giveTalent),
//                        exchangePost.receiveTalents.any().talentCode.talentName,
//                        exchangePost.createdAt,
//                        exchangePost.count,
//                        favoriteExchangePost.exchangePostNo.count().intValue())
//        ).from(exchangePost)
//                .innerJoin(user)
//                .on(exchangePost.user.userNo.eq(user.userNo))
//                .fetch();
//
//        log.info("Select 문 : {} ", result);

        return null;
    }

    /** 재능 교환 게시글의 주고 싶은 재능의 대분류가 매개변수 값과 같은 조건 탐색*/
    private BooleanExpression filterCategories(List<String> categories){
        List<Integer> result = PostUtil.filterValidIntegers(categories);
        return !result.isEmpty() ? exchangePost.giveTalents.any().talentCode.bigCategory.categoryCode.in(result) : null;
    }

    /** 재능 코드 목록을 올바른 값들만 반환하여 조건 탐색*/
    private BooleanExpression filterTalentCodes(List<String> talents){
        List<Integer> result = PostUtil.filterValidIntegers(talents);
        return !result.isEmpty() ? exchangePost.giveTalents.any().talentCode.talentCode.in(result): null;
    }



}
