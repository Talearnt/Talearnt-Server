package com.talearnt.comment.community.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.talearnt.comment.community.entity.QCommunityComment;
import com.talearnt.comment.community.request.CommentSearchCondition;
import com.talearnt.comment.community.response.CommentListResDTO;
import com.talearnt.post.community.entity.QCommunityPost;
import com.talearnt.reply.community.entity.QCommunityReply;
import com.talearnt.user.infomation.entity.QUser;
import com.talearnt.util.pagination.PagedData;
import com.talearnt.util.pagination.PagedListWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentQueryRepository {

    private final JPAQueryFactory factory;
    private final QCommunityComment comment = QCommunityComment.communityComment;
    private final QCommunityPost communityPost = QCommunityPost.communityPost;
    private final QCommunityReply reply = QCommunityReply.communityReply;
    private final QUser user = QUser.user;

    //커뮤니티 댓글 삭제
    public Long deleteCommentByUserNoAndCommentNo(Long userNo, Long commentNo) {
        return factory.update(comment)
                .set(comment.deletedAt, LocalDateTime.now())
                .where(comment.user.userNo.eq(userNo),
                        comment.commentNo.eq(commentNo),
                        comment.deletedAt.isNull())
                .execute();
    }

    /**
     * 커뮤니티 댓글 수정
     *
     * @return Long : 업데이트 된 게시글 갯수
     */
    public Long updateCommentByUserNoAndCommentNo(Long userNo, Long commentNo, String content) {

        return factory.update(comment)
                .set(comment.content, content)
                .set(comment.updatedAt, LocalDateTime.now())
                .where(comment.commentNo.eq(commentNo),
                        comment.user.userNo.eq(userNo),
                        comment.deletedAt.isNull())
                .execute();
    }


    /**
     * 커뮤니티 댓글 수정 전 권환 확인- 나의 댓글이 맞고, 삭제된 댓글이 아닌가?
     *
     * @return true : 내 댓글이 맞음
     * false : 내 댓글이 아님
     */
    public Boolean isMyCommentAndIsNotDeleted(Long userNo, Long commentNo) {
        return factory.select(comment.commentNo)
                .from(comment)
                .where(comment.user.userNo.eq(userNo),
                        comment.commentNo.eq(commentNo),
                        comment.deletedAt.isNull())
                .fetchOne() != null;
    }

    /**댓글 추가 시 웹에서 Offset을 설정하기 위해 총 댓글 수를 호출*/
    public Long getCommentTotalCount(Long postNo){
        return Optional.ofNullable(
                factory.select(comment.countDistinct())
                        .from(comment)
                        .where(comment.deletedAt.isNull(),
                                comment.communityPost.communityPostNo.eq(postNo))
                        .fetchOne()
        ).orElse(0L);
    }


    /**
     * 커뮤니티 댓글 목록 - 모바일 전용
     */
    public Page<CommentListResDTO> getCommentListToMobile(Long postNo, CommentSearchCondition condition, String path) {

        //최신순, 오래된순
        List<CommentListResDTO> data = getListSeleted()
                .where(
                        comment.deletedAt.isNull(),
                        comment.communityPost.communityPostNo.eq(postNo),
                        lastNoLt(condition.getLastNo()) // LastNo가 있을 경우 보다 큰거 거 반환 - 오래된 순임
                )
                .groupBy(comment.commentNo)
                .orderBy(orderByPathAndLastNo(path, condition.getLastNo()))// 최신순, 오래된 순
                .limit(condition.getPage().getPageSize())
                .fetch();

        //데이터 정렬이 최신순일 때 오래된 순으로 변경
        if ("mobile".equalsIgnoreCase(path) && condition.getLastNo() == null) {
            data = data.stream()
                    .sorted((d1, d2) -> d1.getCommentNo().compareTo(d2.getCommentNo()))
                    .toList();
        }

        Long total = Optional.ofNullable(
                factory.select(comment.countDistinct())
                        .from(comment)
                        .where(
                                comment.deletedAt.isNull(),
                                comment.communityPost.communityPostNo.eq(postNo),
                                lastNoLt(condition.getLastNo())
                        ).fetchOne()
        ).orElse(0L);

        return new PageImpl<>(data, condition.getPage(), total);
    }


    /**
     * 커뮤니티 댓글 목록 - 웹 전용
     */
    public PagedListWrapper<CommentListResDTO> getCommentListToWeb(Long postNo, CommentSearchCondition condition) {
        List<CommentListResDTO> data = getListSeleted()
                .where(
                        comment.deletedAt.isNull(),// 댓글이 삭제되지 않았고,
                        comment.communityPost.communityPostNo.eq(postNo)//커뮤니티 게시글 번호가 같고
                )
                .groupBy(comment.commentNo)
                .offset(condition.getPage().getOffset())
                .limit(condition.getPage().getPageSize())
                .fetch();

        PagedData pagedData = factory.select(Projections.constructor(PagedData.class,
                        comment.countDistinct(),
                        Expressions.dateTemplate(LocalDateTime.class,
                                "MAX({0})",
                                comment.createdAt)))
                .from(comment)
                .where(
                        comment.deletedAt.isNull(),// 삭제되지 않은 댓글
                        comment.communityPost.communityPostNo.eq(postNo)//게시물 번호와 같은 댓글
                )
                .fetchOne();

        return PagedListWrapper.<CommentListResDTO>builder().list(data).pagedData(pagedData).build();
    }

    /**
     * 커뮤니티 댓글 목록 조회 공통 Select 반환
     */
    public JPAQuery<CommentListResDTO> getListSeleted() {
        return factory.select(Projections.constructor(CommentListResDTO.class,
                        user.userNo,
                        user.nickname,
                        user.profileImg,

                        comment.commentNo,
                        comment.content,
                        comment.createdAt,
                        comment.updatedAt,
                        reply.countDistinct()
                ))
                .from(comment)
                .leftJoin(communityPost).on(communityPost.eq(comment.communityPost))
                .leftJoin(reply).on(reply.communityComment.eq(comment))
                .leftJoin(user).on(user.eq(comment.user));
    }

    /**
     * 커뮤니티 게시글 LastNo가 있을 경우 현재 댓글 번호보다 큰 거, 오래된 순으로 가져오기 때문에
     */
    private BooleanExpression lastNoLt(Long lastNo) {
        return lastNo != null ? comment.commentNo.lt(lastNo) : null;
    }

    /**
     * 경로가 모바일이고, lastNo가 없을 경우 최신순 데이터를 뽑도록 유도 아닐 경우 오래된 순으로
     */
    private OrderSpecifier<Long> orderByPathAndLastNo(String path, Long lastNo) {
        if (lastNo == null && "mobile".equalsIgnoreCase(path)) {
            return comment.commentNo.desc();
        }
        return comment.commentNo.asc();
    }
}
