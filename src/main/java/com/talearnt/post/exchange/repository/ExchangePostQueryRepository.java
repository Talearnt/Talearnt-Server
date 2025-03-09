package com.talearnt.post.exchange.repository;


import com.querydsl.core.Tuple;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.talearnt.admin.category.entity.QTalentCategory;
import com.talearnt.chat.entity.QChatRequest;
import com.talearnt.chat.entity.QChatRoom;
import com.talearnt.enums.post.ExchangePostStatus;
import com.talearnt.enums.post.ExchangeType;
import com.talearnt.enums.post.PostType;
import com.talearnt.post.exchange.entity.*;
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
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

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

    //재능 교환 게시글 삭제
    public long deleteExchangePostByPostNo(Long postNo) {
        return factory.update(exchangePost)
                .set(exchangePost.deletedAt, LocalDateTime.now())
                .where(exchangePost.exchangePostNo.eq(postNo))
                .execute();
    }


    //받고 싶은 재능 삭제
    public void deleteReceiveTalents(List<Long> ids) {
        if (ids.isEmpty()) return;

        factory.delete(receiveTalent)
                .where(receiveTalent.wantReceiveTalentNo.in(ids))
                .execute();
    }


    //받고 싶은 재능 업데이트
    public void updateReceiveTalents(Map<Long, Integer> updateReceiveTalentCodes) {
        if (updateReceiveTalentCodes.isEmpty()) return;

        updateReceiveTalentCodes.forEach((key, value) -> factory.update(receiveTalent)
                .set(receiveTalent.talentCode.talentCode, value)
                .where(receiveTalent.wantReceiveTalentNo.eq(key))
                .execute());
    }

    //주고 싶은 재능 삭제
    public void deleteGiveTalents(List<Long> ids) {
        if (ids.isEmpty()) return;

        factory.delete(giveTalent)
                .where(giveTalent.wantGiveTalentNo.in(ids))
                .execute();
    }

    //주고 싶은 재능 업데이트
    public void updateGiveTalents(Map<Long, Integer> updateGiveTalentCodes) {
        if (updateGiveTalentCodes.isEmpty()) return;

        updateGiveTalentCodes.forEach((key, value) -> factory.update(giveTalent)
                .set(giveTalent.talentCode.talentCode, value)
                .where(giveTalent.wantGiveTalentNo.eq(key))
                .execute());
    }

    //주고 싶은, 받고 싶은 텔런트 코드 추출
    public Map<String, List<Tuple>> getGiveAndReceiveTalentCodesByPostNo(Long postNo) {
        Map<String, List<Tuple>> codes = new HashMap<>();

        codes.put("giveTalentCodes", factory.select(giveTalent.wantGiveTalentNo, giveTalent.talentCode.talentCode)
                .from(giveTalent)
                .where(giveTalent.exchangePost.exchangePostNo.eq(postNo))
                .fetch());

        codes.put("receiveTalentCodes", factory.select(receiveTalent.wantReceiveTalentNo, receiveTalent.talentCode.talentCode)
                .from(receiveTalent)
                .where(receiveTalent.exchangePost.exchangePostNo.eq(postNo))
                .fetch());

        return codes;
    }

    /**
     * ExchangePost 수정
     */
    public long updateExchangePost(Long postNo, String title, String content, ExchangeType exchangeType, boolean requiredBadge, String duration) {
        return factory.update(exchangePost)
                .set(exchangePost.title, title)
                .set(exchangePost.content, content)
                .set(exchangePost.exchangeType, exchangeType)
                .set(exchangePost.requiredBadge, requiredBadge)
                .set(exchangePost.duration, duration)
                .where(exchangePost.exchangePostNo.eq(postNo)).execute();
    }

    /**
     * 나의 재능 이력 가져오기
     */
    public List<Integer> getPastMyTalents(Long currentUserNo) {
        QMyTalent myTalent = QMyTalent.myTalent;
        return factory.select(myTalent.talentCategory.talentCode)
                .from(myTalent)
                .where(myTalent.user.userNo.eq(currentUserNo),
                        myTalent.type.eq(false))
                .fetch();
    }

    /**
     * 내 게시글이 맞는지 확인 - 수정 페이지
     */
    public boolean isMyExchangePost(Long postNo, Long userNo) {
        return factory.selectOne()
                .from(exchangePost)
                .where(exchangePost.exchangePostNo.eq(postNo),
                        exchangePost.user.userNo.eq(userNo))
                .fetchFirst() != null;
    }


    /**
     * 활성화된 나의 주고 싶은 재능들 가져오기
     */
    public List<Integer> getWantGiveMyTalents(Long userNo) {
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


    /**
     * 재능 교환 게시글 상세보기
     */
    @Transactional
    public Optional<ExchangePostDetailResDTO> getPostDetail(Long postNo, Long currentUserNo) {
        QFileUpload fileUpload = QFileUpload.fileUpload;
        QTalentCategory giveCategory = new QTalentCategory("giveCategory");
        QTalentCategory receiveCategory = new QTalentCategory("receiveCategory");

        factory.update(exchangePost)
                .set(exchangePost.count, exchangePost.count.add(1))
                .where(exchangePost.exchangePostNo.eq(postNo))
                .execute();

        return Optional.ofNullable(
                factory
                        .select(Projections.constructor(ExchangePostDetailResDTO.class,
                                user.userNo,
                                user.nickname,
                                user.profileImg,
                                user.authority,
                                exchangePost.exchangePostNo,
                                Expressions.stringTemplate("GROUP_CONCAT(DISTINCT {0})",giveCategory.talentName),
                                Expressions.stringTemplate("GROUP_CONCAT(DISTINCT {0})",receiveCategory.talentName),
                                exchangePost.exchangeType,
                                exchangePost.status,
                                exchangePost.createdAt,
                                exchangePost.duration,
                                exchangePost.requiredBadge,
                                Expressions.booleanTemplate("MAX(CASE WHEN {0} THEN 1 ELSE 0 END) = 1",favoriteExchangePost.userNo.eq(currentUserNo)),
                                exchangePost.title,
                                exchangePost.content,
                                Expressions.stringTemplate("function('CUSTOM_GROUP_CONCAT_ASC',{0},{1})",fileUpload.url, fileUpload.fileUploadNo),
                                exchangePost.count,
                                favoriteExchangePost.countDistinct(),
                                chatRequest.countDistinct(),
                                Expressions.numberTemplate(Long.class,"MAX({0})",chatRoom.roomNo)
                        ))
                        .from(exchangePost)
                        .leftJoin(user).on(user.eq(exchangePost.user))
                        .leftJoin(giveTalent).on(giveTalent.exchangePost.eq(exchangePost))
                        .leftJoin(receiveTalent).on(receiveTalent.exchangePost.eq(exchangePost))
                        .leftJoin(favoriteExchangePost).on(favoriteExchangePost.exchangePostNo.eq(exchangePost.exchangePostNo))
                        .leftJoin(giveCategory).on(giveCategory.talentCode.eq(giveTalent.talentCode.talentCode))
                        .leftJoin(receiveCategory).on(receiveCategory.talentCode.eq(receiveTalent.talentCode.talentCode))
                        .leftJoin(fileUpload).on(fileUpload.postNo.eq(exchangePost.exchangePostNo),
                                fileUpload.postType.eq(PostType.EXCHANGE))
                        .leftJoin(chatRoom).on(chatRoom.exchangePost.eq(exchangePost))
                        .leftJoin(chatRequest).on(chatRequest.chatRoom.eq(chatRoom))
                        .where(exchangePost.exchangePostNo.eq(postNo),
                                exchangePost.deletedAt.isNull())
                        .groupBy(exchangePost.exchangePostNo)
                        .fetchOne()
        );
    }


    /**
     * 재능 교환 목록 불러오기 (Filter 조건)<br>
     */
    public Page<ExchangePostListResDTO> getFilteredExchangePostList(ExchangeSearchConditionDTO searchConditionDTO, Long currentUserNo) {

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
                        Expressions.stringTemplate("SUBSTRING(CAST(REGEXP_REPLACE({0}, '<[^>]*>', '') AS STRING), 1, 100)", exchangePost.content),
                        Expressions.stringTemplate("GROUP_CONCAT(DISTINCT {0})", JPAExpressions
                                .select(talentCategory.talentName)
                                .from(talentCategory)
                                .where(talentCategory.talentCode.eq(giveTalent.talentCode.talentCode))
                                .groupBy(giveTalent.exchangePost.exchangePostNo)
                        ),
                        Expressions.stringTemplate("GROUP_CONCAT(DISTINCT {0})", JPAExpressions
                                .select(talentCategory.talentName)
                                .from(talentCategory)
                                .where(talentCategory.talentCode.eq(receiveTalent.talentCode.talentCode))
                                .groupBy(receiveTalent.exchangePost.exchangePostNo)
                        ),
                        exchangePost.createdAt,
                        Expressions.numberTemplate(Long.class,
                                "COALESCE(({0}), 0)",
                                JPAExpressions
                                        .select(chatRequest.count())
                                        .from(chatRequest)
                                        .where(chatRequest.chatRoom.roomNo.eq(chatRoom.roomNo))
                                        .groupBy(chatRequest.chatRoom.roomNo),
                                "openedChatRoomCount"),
                        favoriteExchangePost.countDistinct().intValue(),
                        JPAExpressions.select(favoriteExchangePost.count().gt(0))
                                .from(favoriteExchangePost)
                                .where(favoriteExchangePost.exchangePostNo.eq(exchangePost.exchangePostNo),
                                        favoriteExchangePost.userNo.eq(currentUserNo),
                                        favoriteExchangePost.deletedAt.isNull())
                )).from(exchangePost)
                .leftJoin(user).on(exchangePost.user.userNo.eq(user.userNo))
                .leftJoin(favoriteExchangePost).on(exchangePost.exchangePostNo.eq(favoriteExchangePost.exchangePostNo),
                        favoriteExchangePost.deletedAt.isNull())
                .leftJoin(giveTalent).on(exchangePost.exchangePostNo.eq(giveTalent.exchangePost.exchangePostNo))
                .leftJoin(receiveTalent).on(exchangePost.exchangePostNo.eq(receiveTalent.exchangePost.exchangePostNo))
                .leftJoin(chatRoom).on(chatRoom.exchangePost.exchangePostNo.eq(exchangePost.exchangePostNo))
                .where(
                        favoriteExchangePost.deletedAt.isNull(), // 찜 게시글이 삭제되지 않았고, -> favoriteExchangePost를 위함
                        exchangePost.deletedAt.isNull(), //게시글이 삭제되지 않았고,
                        titleLike(searchConditionDTO.getSearch()), //검색 키워드가 존재하고
                        giveTalentsCodeEq(searchConditionDTO.getGiveTalents()),//주고 싶은 재능 분류의 코드가 일치하고,
                        receiveTalentCodesEq(searchConditionDTO.getReceiveTalents()),//받고 싶은 재능 분류의 코드가 일치할 경우
                        durationEq(searchConditionDTO.getDuration()),//진행 기간이 일치하고
                        exchangeTypeEq(searchConditionDTO.getType()), //진행 방식이 일치하고
                        requiredBadgeEq(searchConditionDTO.getRequiredBadge()), //인증 뱃지 여부가 일치하고
                        exchangePostStatusEq(searchConditionDTO.getStatus()), // 모집 상태가 일치하고
                        lastNoLt(searchConditionDTO.getLastNo()) //마지막 번호보다 작고
                )
                .orderBy(orderEq(searchConditionDTO.getOrder()).toArray(new OrderSpecifier[0]))// 최신순, 인기순으로 정렬
                .groupBy(exchangePost.exchangePostNo,
                        chatRoom.roomNo)
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
                                giveTalentsCodeEq(searchConditionDTO.getGiveTalents()),//대분류 코드가 일치하고,
                                receiveTalentCodesEq(searchConditionDTO.getReceiveTalents()),//재능 분류의 코드가 일치할 경우
                                durationEq(searchConditionDTO.getDuration()),//진행 기간이 일치하고
                                exchangeTypeEq(searchConditionDTO.getType()), //진행 방식이 일치하고
                                requiredBadgeEq(searchConditionDTO.getRequiredBadge()), //인증 뱃지 여부가 일치하고
                                exchangePostStatusEq(searchConditionDTO.getStatus()) // 모집 상태가 일치하고
                        ).fetchOne()
        ).orElse(0L);


        return new PageImpl<>(data, searchConditionDTO.getPage(), total);
    }

    /**
     * 재능 교환 제목 검색(title)가 searchTitle과 같은 값만 조건 탐색.
     */
    private BooleanExpression titleLike(String searchKeyword) {
        if (searchKeyword == null || searchKeyword.isEmpty()) return null;
        final String formattedSearchKeyword = "\"" + searchKeyword + "\"";

        return Expressions.numberTemplate(Double.class,
                "function('match',{0},{1})",
                QExchangePost.exchangePost.title,
                formattedSearchKeyword
        ).gt(0);
    }

    /**
     * 주고 싶은 재능 분류 키워드에 해당하는 값들 가져오기 <br>
     * PostUtil 의 filterValidIntegers 에서 정제를 커친 후 사용함
     */
    private BooleanExpression giveTalentsCodeEq(List<Integer> giveTalents) {
        return !giveTalents.isEmpty() ? exchangePost.giveTalents.any().talentCode.talentCode.in(giveTalents) : null;
    }

    /**
     * 받고 싶은 재능 분류 키워드에 해당하는 값들 가져오기<br>
     * PostUtil 의 filterValidIntegers 에서 정제를 커친 후 사용함
     */
    private BooleanExpression receiveTalentCodesEq(List<Integer> receiveTalents) {
        return !receiveTalents.isEmpty() ? exchangePost.receiveTalents.any().talentCode.talentCode.in(receiveTalents) : null;
    }

    /**
     * 정렬 방식(Order) - Order By절 -> 최신순, 인기순<br>
     * PostUtil 의 filterValidOrderValue 에서 정제를 커친 후 사용함
     */
    private List<OrderSpecifier<?>> orderEq(String order) {
        //인기 순일 경우
        if ("popular".equals(order)) {
            LocalDateTime now = LocalDateTime.now();
            return List.of(
                    Expressions.cases().when(exchangePost.createdAt.between(now.minusHours(24), now))//24시간 이내로
                            .then(1)//우선순위 1
                            .otherwise(2).asc() //나머지는 2순위, then = 1, otherwise = 2 에 대한 .asc
                    , exchangePost.count.desc() // 게시글 조회수 순
                    , exchangePost.exchangePostNo.desc()); // 게시글 최신순
        }
        // 죄신순일 경우
        return List.of(exchangePost.exchangePostNo.desc());
    }

    private BooleanExpression lastNoLt(Long lastNo) {
        return lastNo != null ? exchangePost.exchangePostNo.lt(lastNo) : null;
    }

    /**
     * 진행 기간(Duration)에 맞는 게시글 가져오기 <br>
     * 이미 PostUtil의 filterValidDurationVlaue 에서 정제를 거친 후 사용함<br>
     * 기간 미정,1개월,2개월,3개월,3개월 이상이 아닌 값은 null
     */
    private BooleanExpression durationEq(String duration) {
        return duration != null ? exchangePost.duration.eq(duration) : null;
    }

    /**
     * 재능 교환 진행 방식 (Exchange Type)이 일치하는 게시글 가져오기<br>
     * PostUtil 의 filterValidExchangeType 에서 정제를 커친 후 사용함<br>
     * 온라인,오프라인,온_오프라인 외 null 값
     */
    private BooleanExpression exchangeTypeEq(ExchangeType type) {
        return type != null ? exchangePost.exchangeType.eq(type) : null;
    }

    /**
     * 인증뱃지 필수 여부 (requiredBadge)<br>
     * PostUtil 의 filterValidRequiredBadge 에서 정제를 커친 후 사용함<br>
     * true,false 외 null
     */
    private BooleanExpression requiredBadgeEq(Boolean requiredBadge) {
        return requiredBadge != null ? exchangePost.requiredBadge.eq(requiredBadge) : null;
    }

    /**
     * 재능 교환 모집 상태 (ExchangePostStatus)<br>
     * PostUtil 의 filterValidExchangePostStatus 에서 정제를 커친 후 사용함<br>
     * 모집중,모집_완료 외 null
     */
    private BooleanExpression exchangePostStatusEq(ExchangePostStatus status) {
        return status != null ? exchangePost.status.eq(status) : null;
    }

}
