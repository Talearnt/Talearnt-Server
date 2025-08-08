package com.talearnt.reply.community.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.talearnt.comment.community.entity.CommunityComment;
import com.talearnt.comment.community.entity.QCommunityComment;
import com.talearnt.comment.community.response.CommentNotificationDTO;
import com.talearnt.post.community.entity.QCommunityPost;
import com.talearnt.reply.community.entity.CommunityReply;
import com.talearnt.reply.community.entity.QCommunityReply;
import com.talearnt.reply.community.request.ReplySearchCondition;
import com.talearnt.reply.community.response.MyRepliesResDTO;
import com.talearnt.reply.community.response.ReplyListResDTO;
import com.talearnt.user.infomation.entity.QUser;
import com.talearnt.util.pagination.PagedData;
import com.talearnt.util.pagination.PagedListWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReplyQueryRepository {

    private final JPAQueryFactory factory;
    private final QCommunityReply reply = QCommunityReply.communityReply;
    private final QCommunityPost communityPost = QCommunityPost.communityPost;
    private final QUser user = QUser.user;
    private final QCommunityComment comment = QCommunityComment.communityComment;


    //커뮤니티 게시글에 달린 댓글에 대한 답글 알림을 조회합니다.
    public Optional<CommentNotificationDTO> getReplyNotification(Long replyNo) {
        return Optional.ofNullable(
                factory.select(Projections.constructor(CommentNotificationDTO.class,
                        reply.user.userNo,
                        reply.user.nickname,
                        reply.communityComment.user.userNo,
                        reply.communityComment.user.userId,
                        reply.communityComment.commentNo,
                        reply.content))
                        .from(reply)
                        .innerJoin(comment).on(comment.commentNo.eq(reply.communityComment.commentNo),
                                comment.deletedAt.isNull())
                        .innerJoin(communityPost).on(communityPost.communityPostNo.eq(comment.communityPost.communityPostNo),
                                communityPost.deletedAt.isNull())
                        .where(
                                reply.replyNo.eq(replyNo),
                                reply.deletedAt.isNull()
                        )
                        .fetchOne()
        );
    }


    public Optional<CommunityReply> findByIdAndNotDeleted(Long replyNo) {
        return Optional.ofNullable(
                factory.selectFrom(reply)
                        .where(
                                reply.deletedAt.isNull(),
                                reply.replyNo.eq(replyNo)
                        )
                        .fetchOne()
        );
    }

    /***
     * 댓글 번호에 해당하는 답글 목록을 가져옵니다.
     * @param commentNo 댓글 번호
     * @param condition 검색 조건
     * @return Page<ReplyListResDTO>
     */
    public Page<ReplyListResDTO> getReplies(Long commentNo,ReplySearchCondition condition) {

        List<ReplyListResDTO> data = factory.select(Projections.constructor(ReplyListResDTO.class,
                        reply.user.userNo,
                        reply.user.nickname,
                        reply.user.profileImg,
                        reply.replyNo,
                        reply.communityComment.commentNo,
                        reply.content,
                        reply.createdAt,
                        reply.updatedAt))
                .from(reply)
                .leftJoin(user).on(user.userNo.eq(reply.user.userNo))
                .where(
                        reply.deletedAt.isNull(), // 삭제된 답글 제외
                        reply.communityComment.commentNo.eq(commentNo),// 댓글 번호 같은 것
                        lastNoLt(condition.getLastNo()) // 첫 답글 번호보다 작은 것
                )
                .orderBy(reply.replyNo.desc())
                .limit(condition.getPage().getPageSize())
                .fetch();

        data = data.stream()
                .sorted((d1, d2) -> d1.getReplyNo().compareTo(d2.getReplyNo()))
                .toList();

        long total = Optional.ofNullable(
                factory.select(reply.countDistinct())
                        .from(reply)
                        .where(
                                reply.deletedAt.isNull(), // 삭제된 답글 제외
                                reply.communityComment.commentNo.eq(commentNo),// 댓글 번호 같은 것
                                lastNoLt(condition.getLastNo()) // 첫 답글 번호보다 작은 것
                        ).fetchOne()
        ).orElse(0L);

        return new PageImpl<>(data,condition.getPage(), total);
    }

    /** 내가 작성한 답글 목록 조회 - 웹 */
    public PagedListWrapper<MyRepliesResDTO> getMyRepliesToWeb(Long userNo, ReplySearchCondition condition) {
        List<MyRepliesResDTO> data = factory
                .select(Projections.constructor(
                        MyRepliesResDTO.class,
                        communityPost.communityPostNo,
                        communityPost.postType,
                        communityPost.title,
                        reply.replyNo,
                        reply.content,
                        reply.createdAt,
                        reply.updatedAt
                ))
                .from(reply)
                .innerJoin(comment).on(comment.commentNo.eq(reply.communityComment.commentNo),
                        comment.deletedAt.isNull())
                .innerJoin(communityPost).on(communityPost.communityPostNo.eq(comment.communityPost.communityPostNo),
                        communityPost.deletedAt.isNull())
                .where(
                        reply.user.userNo.eq(userNo),
                        reply.deletedAt.isNull()
                )
                .orderBy(reply.replyNo.desc())
                .offset(condition.getPage().getOffset())
                .limit(condition.getPage().getPageSize())
                .fetch();

        PagedData pagedData = factory.select(Projections.constructor(PagedData.class,
                reply.countDistinct(),
                Expressions.dateTemplate(LocalDateTime.class,
                        "MAX({0})",
                        reply.createdAt)))
                .from(reply)
                .innerJoin(comment).on(comment.commentNo.eq(reply.communityComment.commentNo),
                        comment.deletedAt.isNull())
                .innerJoin(communityPost).on(communityPost.communityPostNo.eq(comment.communityPost.communityPostNo),
                        communityPost.deletedAt.isNull())
                .where(
                        reply.user.userNo.eq(userNo),
                        reply.deletedAt.isNull()
                )
                .fetchOne();

        return PagedListWrapper.<MyRepliesResDTO>builder().list(data).pagedData(pagedData).build();

    }
    
    /** 내가 작성한 답글 목록 조회 - 모바일*/
    public Page<MyRepliesResDTO> getMyRepliesToMobile(Long userNo, ReplySearchCondition condition) {
        List<MyRepliesResDTO> content = factory
                .select(Projections.constructor(
                        MyRepliesResDTO.class,
                        communityPost.communityPostNo,
                        communityPost.postType,
                        communityPost.title,
                        reply.replyNo,
                        reply.content,
                        reply.createdAt,
                        reply.updatedAt
                ))
                .from(reply)
                .innerJoin(comment).on(comment.commentNo.eq(reply.communityComment.commentNo),
                        comment.deletedAt.isNull())
                .innerJoin(communityPost).on(communityPost.communityPostNo.eq(comment.communityPost.communityPostNo),
                        communityPost.deletedAt.isNull())
                .where(
                        reply.user.userNo.eq(userNo),
                        reply.deletedAt.isNull(),
                        lastNoLt(condition.getLastNo()) // 마지막 답글 번호보다 작은 것
                )
                .orderBy(reply.replyNo.desc())
                .limit(condition.getPage().getPageSize())
                .fetch();

        long total = Optional.ofNullable(
                factory.select(reply.count())
                        .from(reply)
                        .where(
                                reply.user.userNo.eq(userNo),
                                reply.deletedAt.isNull()
                        ).fetchOne()
        ).orElse(0L);

        return new PageImpl<>(content, condition.getPage(), total);
    }



    /***
     * 마지막 답글 번호보다 큰 답글을 가져오는 조건을 생성합니다.
     * @param lastNo 마지막 답글 번호
     * @return BooleanExpression
     */
    private BooleanExpression lastNoLt(Long lastNo) {
        return lastNo != null ? reply.replyNo.lt(lastNo) : null;
    }
}
