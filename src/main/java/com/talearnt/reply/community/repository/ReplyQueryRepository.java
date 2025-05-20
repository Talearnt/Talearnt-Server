package com.talearnt.reply.community.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.talearnt.reply.community.entity.CommunityReply;
import com.talearnt.reply.community.entity.QCommunityReply;
import com.talearnt.reply.community.request.ReplySearchCondition;
import com.talearnt.reply.community.response.ReplyListResDTO;
import com.talearnt.user.infomation.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReplyQueryRepository {

    private final JPAQueryFactory factory;
    private final QCommunityReply reply = QCommunityReply.communityReply;
    private final QUser user = QUser.user;

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


    /***
     * 마지막 답글 번호보다 큰 답글을 가져오는 조건을 생성합니다.
     * @param lastNo 마지막 답글 번호
     * @return BooleanExpression
     */
    private BooleanExpression lastNoLt(Long lastNo) {
        return lastNo != null ? reply.replyNo.lt(lastNo) : null;
    }
}
