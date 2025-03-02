package com.talearnt.post.community.repository;


import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.talearnt.post.community.entity.QCommunityComment;
import com.talearnt.post.community.entity.QCommunityPost;
import com.talearnt.post.community.entity.QCommunityReply;
import com.talearnt.post.community.entity.QLikeCommunity;
import com.talearnt.post.community.response.CommunityPostDetailResDTO;
import com.talearnt.user.infomation.entity.QUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@Log4j2
@RequiredArgsConstructor
public class CommunityPostQueryRepository {

    private final JPAQueryFactory factory;
    private final QCommunityPost communityPost = QCommunityPost.communityPost;
    private final QUser user = QUser.user;
    private final QCommunityComment communityComment = QCommunityComment.communityComment;
    private final QCommunityReply communityReply = QCommunityReply.communityReply;
    private final QLikeCommunity likeCommunity = QLikeCommunity.likeCommunity;


    //커뮤니티 게시글 삭제
    public long deleteCommunityPostByPostNo(Long postNo){
        return factory.update(communityPost)
                .set(communityPost.deletedAt, LocalDateTime.now())
                .where(communityPost.communityPostNo.eq(postNo),
                        communityPost.deletedAt.isNull())
                .execute();
    }

    //커뮤니티 게시글 삭제가 되었는지 확인 true == 삭제된 게시글, false == 삭제되지 않은 게시글
    public boolean isDeletedCommunityPost(Long postNo){
        return factory
                .selectOne()
                .from(communityPost)
                .where(communityPost.deletedAt.isNull(),
                        communityPost.communityPostNo.eq(postNo))
                .fetchFirst() == null;
    }

    //커뮤니티 게시글 본인 게시글 맞는지 확인 true == 내 게시글 아님, false == 내 게시글임
    public boolean isMyCommunityPostByUserNo(Long postNo, Long userNo){
        return factory.selectOne()
                .from(communityPost)
                .where(communityPost.communityPostNo.eq(postNo),
                        communityPost.user.userNo.eq(userNo))
                .fetchFirst() == null;
    }

    //커뮤니티 게시글 상세보기
    public Optional<CommunityPostDetailResDTO> getCommunityPostByPostNo(Long currentPostNo, Long currentUserNo) {

        //조회수 증가
        factory.update(communityPost).set(communityPost.count, communityPost.count.add(1))
                .where(communityPost.communityPostNo.eq(currentPostNo))
                .execute();
        
        return Optional.ofNullable(
                factory.select(Projections.constructor(
                                CommunityPostDetailResDTO.class,
                                user.userNo,
                                user.nickname,
                                user.profileImg,
                                user.authority,

                                communityPost.title,
                                communityPost.content,
                                communityPost.postType,
                                communityPost.count,
                                Expressions.booleanTemplate("CASE WHEN {0} IS NOT NULL THEN true ELSE false END", likeCommunity.communityPost.communityPostNo),
                                ExpressionUtils.as(
                                        JPAExpressions.select(communityComment.countDistinct().add(communityReply.countDistinct()))
                                                .from(communityComment)
                                                .leftJoin(communityReply).on(communityReply.communityComment.eq(communityComment))
                                                .where(communityComment.communityPost.eq(communityPost)),
                                        "commentCount"
                                ),
                                communityPost.createdAt
                        ))
                        .from(communityPost)
                        .leftJoin(user).on(user.eq(communityPost.user))
                        .leftJoin(communityComment).on(communityComment.communityPost.eq(communityPost))
                        .leftJoin(likeCommunity).on(likeCommunity.communityPost.eq(communityPost)
                                .and(likeCommunity.userNo.eq(currentUserNo))
                                .and(likeCommunity.canceledAt.isNull()))
                        .where(communityPost.deletedAt.isNull(),
                                communityPost.communityPostNo.eq(currentPostNo))
                        .groupBy(communityPost)
                        .fetchOne()
        );
    }

}
