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
import com.talearnt.user.infomation.entity.QUser;
import com.talearnt.user.infomation.response.UserActivityCountsResDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;

import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
@Log4j2
public class UserQueryRepository {
    private final JPAQueryFactory factory;

    private final QUser user = QUser.user;
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


    /**
     * 회원 가입하려는 사람의 휴대폰 번호의 중복 여부를 조회합니다.
     * - 해당 휴대폰 번호로 회원 가입한 사람
     * - 탈퇴한 회원 중 7일이 지나지 않은 휴대폰 번호와 동일한 사람
     * @param phone 회원 가입 하려는 유저의 휴대폰 번호
     * @return 회원 가입 가능 여부
     */
    public boolean isDuplicationPhoneNumberWithUserAndDrawnUser(String phone){
        
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        return factory.selectFrom(user)
        .where(user.phone.eq(phone) //휴대폰 번호가 같거나
                .or(
                        user.isWithdrawn.eq(true) //탈퇴한 회원인데
                        .and(user.withdrawnAt.goe(sevenDaysAgo)) //7일이 지나지 않아서
                        .and(user.withdrawnPhoneNumber.eq(phone))) //휴대폰 번호가 동일한 사람
                )
        .orderBy(user.registeredAt.desc())
        .fetchFirst() != null;
    }

    /**
     * 회원 가입하려는 사람의 이메일 계정(아이디)의 중복 여부를 조회합니다.
     * - 해당 아이디로 회원가입한 사람
     * - 탈퇴한 회원 중 7일이 지나지 않은 아이디 정보와 동일한 사람
     * @param userId 회원 가입 하려는 유저의 이메일 계정(아이디)
     * @return 회원가입 가능 여부
     */
    public boolean isDuplicationUserIdWithUserAndDrawnUser(String userId){
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        return factory.selectFrom(user)
        .where(user.userId.eq(userId) //유저 아이디가 같거나
                .or(
                        user.isWithdrawn.eq(true) //탈퇴한 회원인데
                        .and(user.withdrawnAt.goe(sevenDaysAgo)) //7일이 지나지 않아서
                        .and(user.widthdrawnUserId.eq(userId))) //유저 아이디가 동일한 사람
                )
        .orderBy(user.registeredAt.desc())
        .fetchFirst() != null;
    }

}
