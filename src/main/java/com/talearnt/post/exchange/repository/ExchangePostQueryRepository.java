package com.talearnt.post.exchange.repository;


import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.talearnt.admin.category.entity.QTalentCategory;
import com.talearnt.chat.entity.QChatRequest;
import com.talearnt.chat.entity.QChatRoom;
import com.talearnt.enums.post.ExchangePostStatus;
import com.talearnt.enums.post.ExchangeType;
import com.talearnt.post.exchange.entity.QExchangePost;
import com.talearnt.post.exchange.entity.QFavoriteExchangePost;
import com.talearnt.post.exchange.entity.QGiveTalent;
import com.talearnt.post.exchange.entity.QReceiveTalent;
import com.talearnt.post.exchange.request.ExchangeSearchConditionDTO;
import com.talearnt.post.exchange.response.ExchangePostDetailResDTO;
import com.talearnt.post.exchange.response.ExchangePostListResDTO;
import com.talearnt.s3.entity.QFileUpload;
import com.talearnt.user.infomation.entity.QUser;
import com.talearnt.user.talent.entity.QMyTalent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private final QChatRequest chatRequest = QChatRequest.chatRequest;
    private final QChatRoom chatRoom = QChatRoom.chatRoom;
    private final QTalentCategory talentCategory = QTalentCategory.talentCategory;

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


    /** 재능 교환 게시글 상세보기*/
    @Transactional
    public Optional<ExchangePostDetailResDTO> getPostDetail(Long postNo, Long currentUserNo){
        QFileUpload fileUpload = QFileUpload.fileUpload;

        factory.update(exchangePost)
                .set(exchangePost.count, exchangePost.count.add(1))
                .where(exchangePost.exchangePostNo.eq(postNo))
                .execute();

        return Optional.ofNullable(
                factory.select(Projections.constructor(ExchangePostDetailResDTO.class,
                        user.userNo,
                        user.nickname,
                        user.profileImg,
                        user.authority,
                        exchangePost.exchangePostNo,
                        Expressions.stringTemplate("GROUP_CONCAT(DISTINCT {0})"
                                ,JPAExpressions
                                        .select(talentCategory.talentName)
                                        .from(talentCategory)
                                        .where(talentCategory.talentCode.eq(giveTalent.talentCode.talentCode))
                                        .groupBy(giveTalent.exchangePost.exchangePostNo)),
                        Expressions.stringTemplate("GROUP_CONCAT(DISTINCT {0})",JPAExpressions
                                .select(talentCategory.talentName)
                                .from(talentCategory)
                                .where(talentCategory.talentCode.eq(receiveTalent.talentCode.talentCode))
                                .groupBy(receiveTalent.exchangePost.exchangePostNo)),
                        exchangePost.exchangeType,
                        exchangePost.status,
                        exchangePost.createdAt,
                        exchangePost.duration,
                        exchangePost.requiredBadge,
                        Expressions.booleanTemplate("CASE WHEN {0} IS NOT NULL THEN true ELSE false END", favoriteExchangePost.exchangePostNo),
                        exchangePost.title,
                        exchangePost.content,
                        Expressions.stringTemplate("GROUP_CONCAT(DISTINCT {0})",JPAExpressions
                                .select(fileUpload.url)
                                .from(fileUpload)
                                .where(fileUpload.postNo.eq(postNo))),
                        exchangePost.count,
                        favoriteExchangePost.exchangePostNo.count(),
                        ExpressionUtils.as(JPAExpressions
                                .select(chatRequest.count())
                                .from(chatRequest)
                                .where(chatRequest.chatRoom.roomNo.eq(chatRoom.roomNo))
                                .groupBy(chatRequest.chatRoom.roomNo),
                                "openedChatRoomCount"),
                        chatRoom.roomNo
                        ))
                        .from(exchangePost)
                        .leftJoin(user).on(user.userNo.eq(exchangePost.user.userNo))
                        .leftJoin(chatRoom).on(chatRoom.exchangePost.exchangePostNo.eq(exchangePost.exchangePostNo))
                        .leftJoin(giveTalent).on(giveTalent.exchangePost.exchangePostNo.eq(exchangePost.exchangePostNo))
                        .leftJoin(receiveTalent).on(receiveTalent.exchangePost.exchangePostNo.eq(exchangePost.exchangePostNo))
                        .leftJoin(favoriteExchangePost).on(favoriteExchangePost.exchangePostNo.eq(exchangePost.exchangePostNo).and(favoriteExchangePost.userNo.eq(currentUserNo)))
                        .where(
                                exchangePost.deletedAt.isNull(),
                                exchangePost.exchangePostNo.eq(postNo)
                        )
                        .groupBy(chatRoom.roomNo)
                        .fetchOne()
        );
    }


    /**재능 교환 목록 불러오기 (Filter 조건)<br>*/
    public Page<ExchangePostListResDTO> getFilteredExchangePostList(ExchangeSearchConditionDTO searchConditionDTO){

        log.info("search keyword : {} ",searchConditionDTO.getSearch());


        List<ExchangePostListResDTO> data = factory
                .select(Projections.constructor(
                        ExchangePostListResDTO.class,
                        user.profileImg,
                        user.nickname,
                        user.authority,
                        exchangePost.exchangePostNo,
                        exchangePost.status,
                        exchangePost.exchangeType,
                        exchangePost.duration,
                        exchangePost.requiredBadge,
                        exchangePost.title,
                        Expressions.stringTemplate("SUBSTRING(CAST(REGEXP_REPLACE({0}, '<[^>]*>', '') AS STRING), 1, 100)",exchangePost.content),
                        Expressions.stringTemplate("GROUP_CONCAT(DISTINCT {0})",JPAExpressions
                                .select(talentCategory.talentName)
                                .from(talentCategory)
                                .where(talentCategory.talentCode.eq(giveTalent.talentCode.talentCode))
                                .groupBy(giveTalent.exchangePost.exchangePostNo)
                        ),
                        Expressions.stringTemplate("GROUP_CONCAT(DISTINCT {0})",JPAExpressions
                                .select(talentCategory.talentName)
                                .from(talentCategory)
                                .where(talentCategory.talentCode.eq(receiveTalent.talentCode.talentCode))
                                .groupBy(receiveTalent.exchangePost.exchangePostNo)
                        ),
                        exchangePost.createdAt,
                        exchangePost.count,
                        favoriteExchangePost.countDistinct().intValue()
                )).from(exchangePost)
                .leftJoin(user).on(exchangePost.user.userNo.eq(user.userNo))
                .leftJoin(favoriteExchangePost).on(exchangePost.exchangePostNo.eq(favoriteExchangePost.exchangePostNo))
                .leftJoin(giveTalent).on(exchangePost.exchangePostNo.eq(giveTalent.exchangePost.exchangePostNo))
                .leftJoin(receiveTalent).on(exchangePost.exchangePostNo.eq(receiveTalent.exchangePost.exchangePostNo))
                .where(
                        favoriteExchangePost.deletedAt.isNull(), // 찜 게시글이 삭제되지 않았고, -> favoriteExchangePost를 위함
                        exchangePost.deletedAt.isNull(), //게시글이 삭제되지 않았고,
                        titleLike(searchConditionDTO.getSearch()), //검색 키워드가 존재하고
                        categoriesEq(searchConditionDTO.getCategories()),//대분류 코드가 일치하고,
                        talentCodesEq(searchConditionDTO.getTalents()),//재능 분류의 코드가 일치할 경우
                        durationEq(searchConditionDTO.getDuration()),//진행 기간이 일치하고
                        exchangeTypeEq(searchConditionDTO.getType()), //진행 방식이 일치하고
                        requiredBadgeEq(searchConditionDTO.getRequiredBadge()), //인증 뱃지 여부가 일치하고
                        exchangePostStatusEq(searchConditionDTO.getStatus()) // 모집 상태가 일치하고
                )
                .orderBy(orderEq(searchConditionDTO.getOrder()).toArray(new OrderSpecifier[0]))// 최신순, 인기순으로 정렬
                .groupBy(exchangePost.exchangePostNo)
                .offset(searchConditionDTO.getPage().getOffset())
                .limit(searchConditionDTO.getPage().getPageSize())
                .fetch();

        Long total = Optional.ofNullable(
                factory
                        .select(exchangePost.count())
                        .from(exchangePost)
                        .where(
                                exchangePost.deletedAt.isNull(), //게시글이 삭제되지 않았고,
                                titleLike(searchConditionDTO.getSearch()), //검색 키워드가 존재하고
                                categoriesEq(searchConditionDTO.getCategories()),//대분류 코드가 일치하고,
                                talentCodesEq(searchConditionDTO.getTalents()),//재능 분류의 코드가 일치할 경우
                                durationEq(searchConditionDTO.getDuration()),//진행 기간이 일치하고
                                exchangeTypeEq(searchConditionDTO.getType()), //진행 방식이 일치하고
                                requiredBadgeEq(searchConditionDTO.getRequiredBadge()), //인증 뱃지 여부가 일치하고
                                exchangePostStatusEq(searchConditionDTO.getStatus()) // 모집 상태가 일치하고
                        ).fetchOne()
        ).orElse(0L);


        return new PageImpl<>(data,searchConditionDTO.getPage(),total);
    }

    /** 재능 교환 제목 검색(title)가 searchTitle과 같은 값만 조건 탐색.*/
    private BooleanExpression titleLike(String searchKeyword){
        if(searchKeyword == null || searchKeyword.isEmpty()) return null;
        final String formattedSearchKeyword = "\"" + searchKeyword + "\"";

        return  Expressions.numberTemplate(Double.class,
                "function('match',{0},{1})",
                QExchangePost.exchangePost.title,
                formattedSearchKeyword
        ).gt(0);
    }

    /** 재능 교환 게시글 대분류(categories)가 매개변수 값과 같은 조건 탐색<br>
     * PostUtil 의 filterValidIntegers 에서 정제를 커친 후 사용함*/
    private BooleanExpression categoriesEq(List<Integer> categories){
        return !categories.isEmpty() ? exchangePost.giveTalents.any().talentCode.bigCategory.categoryCode.in(categories)
                .or(exchangePost.receiveTalents.any().talentCode.bigCategory.categoryCode.in(categories)) : null;
    }

    /** 재능 코드 목록(Talents)을 올바른 값들만 반환하여 조건 탐색<br>
     * PostUtil 의 filterValidIntegers 에서 정제를 커친 후 사용함*/
    private BooleanExpression talentCodesEq(List<Integer> talents){
        return !talents.isEmpty() ? exchangePost.giveTalents.any().talentCode.talentCode.in(talents)
                .or(exchangePost.receiveTalents.any().talentCode.talentCode.in(talents)): null;
    }

    /**  정렬 방식(Order) - Order By절 -> 최신순, 인기순<br>
     * PostUtil 의 filterValidOrderValue 에서 정제를 커친 후 사용함*/
    private List<OrderSpecifier<?>> orderEq(String order){
        //인기 순일 경우
        if("popular".equals(order)){
            LocalDateTime now = LocalDateTime.now();
            return List.of(
                    Expressions.cases().when(exchangePost.createdAt.between(now.minusHours(24),now))//24시간 이내로
                            .then(1)//우선순위 1
                            .otherwise(2).asc() //나머지는 2순위, then = 1, otherwise = 2 에 대한 .asc
                    , exchangePost.count.desc() // 게시글 조회수 순
                    , exchangePost.exchangePostNo.desc()); // 게시글 최신순
        }
        // 죄신순일 경우
        return List.of(exchangePost.exchangePostNo.desc());
    }

    /** 진행 기간(Duration)에 맞는 게시글 가져오기 <br>
     * 이미 PostUtil의 filterValidDurationVlaue 에서 정제를 거친 후 사용함<br>
     * 기간 미정,1개월,2개월,3개월,3개월 이상이 아닌 값은 null*/
    private BooleanExpression durationEq(String duration){
        return duration != null ? exchangePost.duration.eq(duration) : null;
    }

    /** 재능 교환 진행 방식 (Exchange Type)이 일치하는 게시글 가져오기<br>
     * PostUtil 의 filterValidExchangeType 에서 정제를 커친 후 사용함<br>
     * 온라인,오프라인,온_오프라인 외 null 값*/
    private BooleanExpression exchangeTypeEq(ExchangeType type){
        return type != null ? exchangePost.exchangeType.eq(type) : null;
    }

    /** 인증뱃지 필수 여부 (requiredBadge)<br>
     * PostUtil 의 filterValidRequiredBadge 에서 정제를 커친 후 사용함<br>
     * true,false 외 null*/
    private BooleanExpression requiredBadgeEq(Boolean requiredBadge){
        return requiredBadge != null ? exchangePost.requiredBadge.eq(requiredBadge) : null;
    }

    /** 재능 교환 모집 상태 (ExchangePostStatus)<br>
     * PostUtil 의 filterValidExchangePostStatus 에서 정제를 커친 후 사용함<br>
     * 모집중,모집_완료 외 null*/
    private BooleanExpression exchangePostStatusEq(ExchangePostStatus status){
        return status != null ? exchangePost.status.eq(status) : null;
    }

}
