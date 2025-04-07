package com.talearnt.post.community.repository;


import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.talearnt.comment.community.entity.QCommunityComment;
import com.talearnt.enums.post.PostType;
import com.talearnt.post.community.entity.CommunityPost;
import com.talearnt.post.community.entity.QCommunityPost;
import com.talearnt.post.community.entity.QLikeCommunity;
import com.talearnt.post.community.request.CommunityPostSearchConditionDTO;
import com.talearnt.post.community.response.CommunityPostDetailResDTO;
import com.talearnt.post.community.response.CommunityPostListResDTO;
import com.talearnt.post.community.response.CommunityPostMobileListResDTO;
import com.talearnt.reply.community.entity.QCommunityReply;
import com.talearnt.s3.entity.QFileUpload;
import com.talearnt.user.infomation.entity.QUser;
import com.talearnt.util.pagination.PagedData;
import com.talearnt.util.pagination.PagedListWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private final QFileUpload fileUpload = QFileUpload.fileUpload;
    private final JdbcTemplate jdbcTemplate;

    public Optional<CommunityPost> findByPostNo(Long postNo) {
        return Optional.ofNullable(
                factory.selectFrom(communityPost)
                        .where(communityPost.communityPostNo.eq(postNo),
                                communityPost.deletedAt.isNull())
                        .fetchOne()
        );
    }


    //커뮤니티 게시글 존재 여부
    public boolean existCommunityByPostNo(Long communityPostNo) {
        return factory.select(communityPost.communityPostNo)
                .from(communityPost)
                .where(communityPost.communityPostNo.eq(communityPostNo),
                        communityPost.deletedAt.isNull())
                .fetchFirst() != null;
    }

    //커뮤니티 게시글 삭제
    public long deleteCommunityPostByPostNo(Long postNo) {
        return factory.update(communityPost)
                .set(communityPost.deletedAt, LocalDateTime.now())
                .where(communityPost.communityPostNo.eq(postNo),
                        communityPost.deletedAt.isNull())
                .execute();
    }

    //커뮤니티 게시글 삭제가 되었는지 확인 true == 삭제된 게시글, false == 삭제되지 않은 게시글
    public boolean isDeletedCommunityPost(Long postNo) {
        return factory
                .selectOne()
                .from(communityPost)
                .where(communityPost.deletedAt.isNull(),
                        communityPost.communityPostNo.eq(postNo))
                .fetchFirst() == null;
    }

    //커뮤니티 게시글 본인 게시글 맞는지 확인 true == 내 게시글 아님, false == 내 게시글임
    public boolean isMyCommunityPostByUserNo(Long postNo, Long userNo) {
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

                                communityPost.communityPostNo,
                                communityPost.title,
                                communityPost.content,
                                communityPost.postType,
                                Expressions.stringTemplate("function('CUSTOM_GROUP_CONCAT_ASC',{0},{1})", fileUpload.url, fileUpload.fileUploadNo),
                                communityPost.count,
                                Expressions.booleanTemplate("MAX(CASE WHEN {0} THEN 1 ELSE 0 END) = 1", likeCommunity.userNo.eq(currentUserNo)),
                                likeCommunity.countDistinct(),
                                communityComment.countDistinct().add(communityReply.countDistinct()),
                                communityPost.createdAt,
                                communityPost.updatedAt
                        ))
                        .from(communityPost)
                        .leftJoin(user).on(user.eq(communityPost.user))
                        .leftJoin(communityComment).on(communityComment.communityPost.eq(communityPost))
                        .leftJoin(communityReply).on(communityReply.communityComment.eq(communityComment))
                        .leftJoin(likeCommunity).on(likeCommunity.communityPost.eq(communityPost)
                                .and(likeCommunity.canceledAt.isNull()))
                        .leftJoin(fileUpload).on(
                                fileUpload.deletedAt.isNull(),
                                fileUpload.postNo.eq(currentPostNo),
                                fileUpload.postType.eq(communityPost.postType)
                        )
                        .where(communityPost.deletedAt.isNull(),
                                communityPost.communityPostNo.eq(currentPostNo))
                        .groupBy(communityPost)
                        .fetchOne()
        );
    }

    public PagedListWrapper<CommunityPostListResDTO> getCommunityPostListToWeb(Long userNo, CommunityPostSearchConditionDTO condition){
        JPAQuery<CommunityPostListResDTO> selected = getSeletedList(condition.getPath(), condition.getOrder(), userNo);

        List<CommunityPostListResDTO> data = selected.from(communityPost)
                .leftJoin(user).on(communityPost.user.userNo.eq(user.userNo))
                .leftJoin(likeCommunity).on(likeCommunity.communityPost.communityPostNo.eq(communityPost.communityPostNo),
                        likeCommunity.canceledAt.isNull())
                .leftJoin(communityComment).on(communityPost.communityPostNo.eq(communityComment.communityPost.communityPostNo),
                        communityComment.deletedAt.isNull())//댓글 조인
                .leftJoin(communityReply).on(communityComment.commentNo.eq(communityReply.communityComment.commentNo),
                        communityReply.deletedAt.isNull())//답글 조인
                .where(communityPost.deletedAt.isNull(),// 게시글이 삭제 되지 않았고
                        postTypeEq(condition.getPostType())// 포스트 타입이 같고, 같지 않을 경우 null == 전체 검색
                )
                .groupBy(communityPost.communityPostNo)
                .orderBy(orderEq(condition.getOrder()).toArray(new OrderSpecifier[0]))
                .offset(condition.getPage().getOffset())
                .limit(condition.getPage().getPageSize())
                .fetch();

        PagedData pagedData = factory.select(Projections.constructor(PagedData.class,
                        communityPost.countDistinct(),
                        Expressions.dateTemplate(LocalDateTime.class,
                                "MAX({0})",
                                communityPost.createdAt)))
                .from(communityPost)
                .where(communityPost.deletedAt.isNull(),// 게시글이 삭제 되지 않았고
                        postTypeEq(condition.getPostType())// 포스트 타입이 같고, 같지 않을 경우 null == 전체 검색
                ).fetchOne();


        return PagedListWrapper.<CommunityPostListResDTO>builder().list(data).pagedData(pagedData).build();
    }



    //커뮤니티 게시글 목록 조회 - 모바일
    public Page<CommunityPostListResDTO> getCommunityPostListToMobile(Long userNo, CommunityPostSearchConditionDTO condition) {
        //마지막 게시글 인기 점수 뽑기 - 커서 기반 페이지네이션
        Double lastPopularScore = null;
        if (condition.getOrder().equalsIgnoreCase("hot") && condition.getLastNo() != null) {
            lastPopularScore = factory.select(Expressions.numberTemplate(
                            Double.class,
                            "({0} * 0.7 + {1} * 0.3)",
                            likeCommunity.countDistinct().coalesce(0L),
                            communityPost.count.coalesce(0)
                    ))
                    .from(communityPost)
                    .leftJoin(likeCommunity).on(likeCommunity.communityPost.eq(communityPost))
                    .where(communityPost.communityPostNo.eq(condition.getLastNo()))
                    .groupBy(communityPost)
                    .fetchOne();
        }

        JPAQuery<CommunityPostListResDTO> selected = getSeletedList(condition.getPath(), condition.getOrder(), userNo);

        List<CommunityPostListResDTO> data = selected.from(communityPost)
                .leftJoin(user).on(communityPost.user.userNo.eq(user.userNo))
                .leftJoin(likeCommunity).on(likeCommunity.communityPost.communityPostNo.eq(communityPost.communityPostNo),
                        likeCommunity.canceledAt.isNull())
                .leftJoin(communityComment).on(communityPost.communityPostNo.eq(communityComment.communityPost.communityPostNo),
                        communityComment.deletedAt.isNull())//댓글 조인
                .leftJoin(communityReply).on(communityComment.commentNo.eq(communityReply.communityComment.commentNo),
                        communityReply.deletedAt.isNull())//답글 조인
                .where(communityPost.deletedAt.isNull(),// 게시글이 삭제 되지 않았고
                        postTypeEq(condition.getPostType()),// 포스트 타입이 같고, 같지 않을 경우 null == 전체 검색
                        lastNoLt(condition.getOrder(), condition.getLastNo())//마지막 게시글 번호보다 작은 것
                )
                .groupBy(communityPost.communityPostNo)
                .having(lastPopularScoreLt(condition.getOrder(), lastPopularScore, condition.getLastNo()))
                .orderBy(orderEq(condition.getOrder()).toArray(new OrderSpecifier[0]))
                .limit(condition.getPage().getPageSize())
                .fetch();
        //인라인 뷰 테이블로 페이지 total 값 가져오기
        Long total = generatedJdbcDynamicQuery(condition.getOrder(), condition.getLastNo(), lastPopularScore);
        return new PageImpl<>(data, condition.getPage(), total);
    }

    private Long generatedJdbcDynamicQuery(String order, Long lastNo, Double lastPopularScore) {
        StringBuffer sql = new StringBuffer();
        List<Object> params = new ArrayList<>();

        sql.append("""
             SELECT count(community.postNo)
             FROM (SELECT (count(DISTINCT lc.like_no) * 0.7 + cp.count * 0.3) AS popular, cp.community_post_no AS postNo
                    FROM talearnt.community_post cp
                    LEFT JOIN talearnt.like_community lc ON lc.community_post_no = cp.community_post_no
                    WHERE cp.deleted_at IS NULL
                """);
        //최근 게시글 정렬이고 LastNo가 있으면 Where절에 추가
        if ("recent".equalsIgnoreCase(order) && lastNo != null) {
            sql.append(" AND cp.community_post_no < ?");
            params.add(lastNo);
        }

        //Group by 추가
        sql.append(" GROUP BY cp.community_post_no");

        //3개의 값이 null이 아니라면 인기 게시글 목록 조회
        if ("hot".equalsIgnoreCase(order) && lastNo != null && lastPopularScore != null) {
            sql.append(" HAVING popular < ? OR popular = ? AND postNo < ?");
            params.add(lastPopularScore);
            params.add(lastPopularScore);
            params.add(lastNo);
        }
        sql.append("""
                    ) AS community 
                    ORDER BY community.popular DESC, community.postNo desc;
                """);

        return jdbcTemplate.queryForObject(sql.toString(),params.toArray(),Long.class);
    }

    //모바일, 웹 공통 Select
    private JPAQuery<CommunityPostListResDTO> getSeletedList(String path, String order, Long userNo) {
        JPAQuery<CommunityPostListResDTO> selected = null;
        //모바일이거나, 웹 핫한 게시글 호출 시
        if (path.equalsIgnoreCase("mobile") || (path.equalsIgnoreCase("web") && order.equalsIgnoreCase("hot"))) {
            selected = factory.select(Projections.constructor(CommunityPostMobileListResDTO.class,
                    user.profileImg,
                    user.nickname,
                    user.authority,
                    communityPost.communityPostNo,
                    communityPost.postType,
                    communityPost.title,
                    communityPost.count,
                    communityComment.countDistinct().add(communityReply.countDistinct()),
                    likeCommunity.countDistinct(),
                    Expressions.booleanTemplate("MAX(CASE WHEN {0} THEN 1 ELSE 0 END) = 1", likeCommunity.userNo.eq(userNo)),
                    communityPost.createdAt,
                    Expressions.stringTemplate("SUBSTRING(CAST(REGEXP_REPLACE({0}, '<[^>]*>', '') AS STRING), 1, 100)", communityPost.content)
            ));
        } else { // 웹 반환
            selected = factory.select(Projections.constructor(CommunityPostListResDTO.class,
                    user.profileImg,
                    user.nickname,
                    user.authority,
                    communityPost.communityPostNo,
                    communityPost.postType,
                    communityPost.title,
                    communityPost.count,
                    communityComment.countDistinct().add(communityReply.countDistinct()),
                    likeCommunity.countDistinct(),
                    Expressions.booleanTemplate("MAX(CASE WHEN {0} THEN 1 ELSE 0 END) = 1", likeCommunity.userNo.eq(userNo)),
                    communityPost.createdAt
            ));
        }
        return selected;
    }
    /***/

    /**
     * 인기순위 점수 구하기 좋아요 * 0.7 + 조회수 * 0.3
     * Hot이 아닐 경우 구해지지 않음,
     * 추후 인기순이 나올 경우 이것으로 계산하고 where절의 baseTimeBetween을 최신순으로 바꾸면 사용 가능
     */
    private BooleanExpression lastPopularScoreLt(String order, Double lastPopularScore, Long lastNo) {
        if (!"hot".equalsIgnoreCase(order) || lastPopularScore == null || lastNo == null) {
            return null;
        }

        NumberExpression<Double> currentPopularScore = Expressions.numberTemplate(
                Double.class,
                "({0} * 0.7 + {1} * 0.3)",
                likeCommunity.countDistinct().coalesce(0L),
                communityPost.count.coalesce(0)
        );

        return currentPopularScore.lt(lastPopularScore)
                .or(
                        currentPopularScore.eq(lastPopularScore)
                                .and(communityPost.communityPostNo.lt(lastNo))
                );

    }


    /**
     * 커뮤니티 게시글 Order
     * 핫한 게시물을 뽑아올 때는 3일 이내
     * 좋아요 * 0.7 , 조회수 0.3으로 점수를 매겨 순서를 정해야 함.
     */
    private List<OrderSpecifier<?>> orderEq(String order) {
        if ("hot".equalsIgnoreCase(order)) {
            NumberTemplate<Double> popularScore = Expressions.numberTemplate(Double.class,
                    "({0} * 0.7 + {1} * 0.3)",
                    likeCommunity.countDistinct().coalesce(0L),
                    communityPost.count.coalesce(0)
            );

            return List.of(popularScore.desc(), communityPost.communityPostNo.desc()); // 핫한 게시글 정렬 정렬
        }
        //최신순
        return order != null ? List.of(communityPost.communityPostNo.desc()) : null;
    }

    /**
     * 최신순일 경우에만 이렇게 구하고, HOT 또는 인기순일 경우에는 lastPopularScore()를 따름
     */
    private BooleanExpression lastNoLt(String order, Long lastNo) {
        if ("recent".equalsIgnoreCase(order) && lastNo != null) return communityPost.communityPostNo.lt(lastNo);
        return null;
    }

    /**
     * 커뮤니티 게시글 타입이 null 일 경우 전체 검색
     * 아닐 경우 해당 게시글 타입 검색
     */
    private BooleanExpression postTypeEq(PostType postType) {
        return postType != null ? communityPost.postType.eq(postType) : null;
    }

}
