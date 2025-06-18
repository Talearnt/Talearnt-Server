package com.talearnt.user.infomation.repository;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.talearnt.comment.community.entity.QCommunityComment;
import com.talearnt.post.community.entity.QCommunityPost;
import com.talearnt.post.exchange.entity.QExchangePost;
import com.talearnt.post.favorite.entity.QFavoriteExchangePost;
import com.talearnt.reply.community.entity.QCommunityReply;
import com.talearnt.user.infomation.response.UserActivityCountsResDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Log4j2
public class UserQueryRepository {
    private final JPAQueryFactory factory;

    private final QExchangePost exchangePost = QExchangePost.exchangePost;
    private final QCommunityPost communityPost = QCommunityPost.communityPost;
    private final QFavoriteExchangePost favoriteExchangePost = QFavoriteExchangePost.favoriteExchangePost;
    private final QCommunityComment comment = QCommunityComment.communityComment;
    private final QCommunityReply reply = QCommunityReply.communityReply;

    public UserActivityCountsResDTO getMyActivityCounts(Long  userNo) {
        log.info("회원의 활동 정보 불러오기 시작");

        Long favoritePostCount = factory.select(favoriteExchangePost.count())
                .from(favoriteExchangePost)
                .where(favoriteExchangePost.userNo.eq(userNo),
                        favoriteExchangePost.deletedAt.isNull())
                .fetchOne();

        Long myPostCount = factory.select(
                        exchangePost.countDistinct().add(
                                JPAExpressions.select(communityPost.countDistinct())
                                        .from(communityPost)
                                        .where(communityPost.user.userNo.eq(userNo),
                                                communityPost.deletedAt.isNull())
                        )
                )
                .from(exchangePost)
                .where(exchangePost.user.userNo.eq(userNo),
                        exchangePost.deletedAt.isNull())
                .fetchOne();

        Long myCommentCount = factory.select(
                        comment.countDistinct().add(
                                JPAExpressions.select(reply.countDistinct())
                                        .from(reply)
                                        .where(reply.user.userNo.eq(userNo),
                                                reply.deletedAt.isNull())
                        )
                )
                .from(comment)
                .where(comment.user.userNo.eq(userNo),
                        comment.deletedAt.isNull())
                .fetchOne();

        return UserActivityCountsResDTO.builder()
                .favoritePostCount(favoritePostCount != null ? favoritePostCount : 0L)
                .myPostCount(myPostCount != null ? myPostCount : 0L)
                .myCommentCount(myCommentCount != null ? myCommentCount : 0L)
                .build();
    }



}
