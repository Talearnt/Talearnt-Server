package com.talearnt.post.favorite.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.talearnt.admin.category.entity.QTalentCategory;
import com.talearnt.chat.entity.QChatRequest;
import com.talearnt.chat.entity.QChatRoom;
import com.talearnt.post.exchange.entity.QExchangePost;
import com.talearnt.post.exchange.entity.QGiveTalent;
import com.talearnt.post.exchange.entity.QReceiveTalent;
import com.talearnt.post.exchange.response.ExchangePostListResDTO;
import com.talearnt.post.favorite.entity.FavoriteExchangePost;
import com.talearnt.post.favorite.entity.QFavoriteExchangePost;
import com.talearnt.post.favorite.request.FavoriteSearchCondition;
import com.talearnt.user.infomation.entity.QUser;
import com.talearnt.util.pagination.PagedData;
import com.talearnt.util.pagination.PagedListWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Log4j2
public class FavoriteExchagePostQueryRepository {
    private final JPAQueryFactory factory;
    private final QFavoriteExchangePost favoriteExchangePost = QFavoriteExchangePost.favoriteExchangePost;
    private final QExchangePost exchangePost = QExchangePost.exchangePost;
    private final QUser user = QUser.user;
    private final QGiveTalent giveTalent = QGiveTalent.giveTalent;
    private final QReceiveTalent receiveTalent = QReceiveTalent.receiveTalent;
    private final QChatRequest chatRequest = QChatRequest.chatRequest;
    private final QChatRoom chatRoom = QChatRoom.chatRoom;
    private final QTalentCategory giveCategory = new QTalentCategory("giveCategory");
    private final QTalentCategory receiveCategory = new QTalentCategory("receiveCategory");


    /** 본인이 추가한 찜 게시글이 있는지 없는지 판별
     * 삭제되었는지 안되었는지는 호출하는 곳에서 판단
     * 삭제되었으면 Deleted At에 시간이 사라지고,
     * 삭제가 되지 않았으면 Deleted At 에 시간이 추가되는 방식*/
    public Optional<FavoriteExchangePost> findByPostNoAndUserId(Long postNo, Long userNo) {
        return Optional.ofNullable(
                factory.selectFrom(favoriteExchangePost)
                        .where(favoriteExchangePost.exchangePostNo.eq(postNo),
                                favoriteExchangePost.userNo.eq(userNo))
                        .fetchOne()
        );
    }

    public PagedListWrapper<ExchangePostListResDTO> getFavoriteExchangePostsToWeb(Long userNo, FavoriteSearchCondition condition) {
        List<ExchangePostListResDTO> data = getListSelected(userNo)
                .where(exchangePost.deletedAt.isNull(),//게시글이 삭제되지 않았고
                        favoriteExchangePost.deletedAt.isNull(),
                        favoriteExchangePost.userNo.eq(userNo),
                        favoriteExchangePost.exchangePostNo.eq(exchangePost.exchangePostNo))
                .groupBy(
                        user.profileImg,
                        user.nickname,
                        user.authority,
                        exchangePost.exchangePostNo,
                        exchangePost.status,
                        exchangePost.exchangeType,
                        exchangePost.duration,
                        exchangePost.requiredBadge,
                        exchangePost.title,
                        exchangePost.content,
                        exchangePost.createdAt,
                        exchangePost.count,
                        favoriteExchangePost.createdAt
                )
                .orderBy(favoriteExchangePost.createdAt.desc())
                .offset(condition.getPage().getOffset())
                .limit(condition.getPage().getPageSize())
                .fetch();

        PagedData pagedData = Optional.ofNullable(
                factory
                        .select(Projections.constructor(PagedData.class,
                                favoriteExchangePost.count(),
                                Expressions.dateTemplate(LocalDateTime.class,
                                        "MAX({0})",
                                        favoriteExchangePost.createdAt)))
                        .from(favoriteExchangePost)
                        .leftJoin(exchangePost).on(favoriteExchangePost.exchangePostNo.eq(exchangePost.exchangePostNo))
                        .where(
                                favoriteExchangePost.deletedAt.isNull(),//게시글이 삭제되지 않았고
                                favoriteExchangePost.userNo.eq(userNo),
                                exchangePost.deletedAt.isNull()//게시글이 삭제되지 않았고
                        ).fetchOne()
        ).orElse(null);

        return PagedListWrapper.<ExchangePostListResDTO>builder().list(data).pagedData(pagedData).build();
    }


    public Page<ExchangePostListResDTO> getFavoriteExchangePostsToMobile(Long userNo, FavoriteSearchCondition condition) {
        List<ExchangePostListResDTO> data = getListSelected(userNo)
                .where(exchangePost.deletedAt.isNull(),//게시글이 삭제되지 않았고
                        favoriteExchangePost.deletedAt.isNull(),
                        favoriteExchangePost.userNo.eq(userNo),
                        favoriteExchangePost.exchangePostNo.eq(exchangePost.exchangePostNo))
                .groupBy(
                        user.profileImg,
                        user.nickname,
                        user.authority,
                        exchangePost.exchangePostNo,
                        exchangePost.status,
                        exchangePost.exchangeType,
                        exchangePost.duration,
                        exchangePost.requiredBadge,
                        exchangePost.title,
                        exchangePost.content,
                        exchangePost.createdAt,
                        exchangePost.count,
                        favoriteExchangePost.createdAt
                )
                .orderBy(favoriteExchangePost.createdAt.desc())
                .offset(condition.getPage().getOffset())
                .limit(condition.getPage().getPageSize())
                .fetch();

        Long total = Optional.ofNullable(
                factory
                        .select(favoriteExchangePost.count())
                        .from(favoriteExchangePost)
                        .where(
                                favoriteExchangePost.userNo.eq(userNo),
                                favoriteExchangePost.deletedAt.isNull()//게시글이 삭제되지 않았고
                        ).fetchOne()
        ).orElse(0L);

        return new PageImpl<>(data, condition.getPage(), total);
    }

    /** 찜 교환 게시글 목록을 불러올 때 사용 (웹/모바일 중복 방지) */
    private JPAQuery<ExchangePostListResDTO> getListSelected(Long userNo) {
        return factory.select(Projections.constructor(ExchangePostListResDTO.class,
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
                                Expressions.stringTemplate("GROUP_CONCAT(DISTINCT {0})", giveCategory.talentName),
                                Expressions.stringTemplate("GROUP_CONCAT(DISTINCT {0})", receiveCategory.talentName),
                                exchangePost.createdAt,
                                exchangePost.count,
                                chatRequest.countDistinct(),
                                favoriteExchangePost.countDistinct(),
                                Expressions.constant(true) // 무조건 True
                        )
                )
                .from(exchangePost)
                .leftJoin(user).on(user.eq(exchangePost.user))
                .leftJoin(giveTalent).on(giveTalent.exchangePost.eq(exchangePost))
                .leftJoin(receiveTalent).on(receiveTalent.exchangePost.eq(exchangePost))
                .leftJoin(favoriteExchangePost).on(favoriteExchangePost.exchangePostNo.eq(exchangePost.exchangePostNo),
                        favoriteExchangePost.deletedAt.isNull(),
                        favoriteExchangePost.userNo.eq(userNo))
                .leftJoin(giveCategory).on(giveCategory.talentCode.eq(giveTalent.talentCode.talentCode))
                .leftJoin(receiveCategory).on(receiveCategory.talentCode.eq(receiveTalent.talentCode.talentCode))
                .leftJoin(chatRoom).on(chatRoom.exchangePost.eq(exchangePost))
                .leftJoin(chatRequest).on(chatRequest.chatRoom.eq(chatRoom));
    }



}
