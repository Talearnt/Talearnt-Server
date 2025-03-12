package com.talearnt.post.community.repository;


import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.talearnt.enums.post.PostType;
import com.talearnt.post.community.entity.QCommunityComment;
import com.talearnt.post.community.entity.QCommunityPost;
import com.talearnt.post.community.entity.QCommunityReply;
import com.talearnt.post.community.entity.QLikeCommunity;
import com.talearnt.post.community.request.CommunityPostSearchConditionDTO;
import com.talearnt.post.community.response.CommunityPostDetailResDTO;
import com.talearnt.post.community.response.CommunityPostListResDTO;
import com.talearnt.post.community.response.CommunityPostMobileListResDTO;
import com.talearnt.s3.entity.QFileUpload;
import com.talearnt.user.infomation.entity.QUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

                                communityPost.communityPostNo,
                                communityPost.title,
                                communityPost.content,
                                communityPost.postType,
                                Expressions.stringTemplate("function('CUSTOM_GROUP_CONCAT_ASC',{0},{1})", fileUpload.url, fileUpload.fileUploadNo),
                                communityPost.count,
                                Expressions.booleanTemplate("MAX(CASE WHEN {0} THEN 1 ELSE 0 END) = 1", likeCommunity.userNo.eq(currentUserNo)),
                                likeCommunity.countDistinct(),
                                communityComment.countDistinct().add(communityReply.countDistinct()),
                                communityPost.createdAt
                        ))
                        .from(communityPost)
                        .leftJoin(user).on(user.eq(communityPost.user))
                        .leftJoin(communityComment).on(communityComment.communityPost.eq(communityPost))
                        .leftJoin(communityReply).on(communityReply.communityComment.eq(communityComment))
                        .leftJoin(likeCommunity).on(likeCommunity.communityPost.eq(communityPost)
                                .and(likeCommunity.canceledAt.isNull()))
                        .leftJoin(fileUpload).on(fileUpload.postNo.eq(currentPostNo),
                                fileUpload.postType.eq(communityPost.postType))
                        .where(communityPost.deletedAt.isNull(),
                                communityPost.communityPostNo.eq(currentPostNo))
                        .groupBy(communityPost)
                        .fetchOne()
        );
    }

    //커뮤니티 게시글 목록 조회
    public Page<CommunityPostListResDTO> getCommunityPostList(Long userNo, CommunityPostSearchConditionDTO condition){
        JPAQuery<CommunityPostListResDTO> selected = null;

        if (condition.getPath().equalsIgnoreCase("mobile")){
            selected = factory.select(Projections.constructor(CommunityPostMobileListResDTO.class,
                    Expressions.numberTemplate(Double.class, "({0} * 0.7 + {1} * 0.3)",
                            likeCommunity.countDistinct().coalesce(0L),
                            communityPost.count.coalesce(0)).as("popularScore"),
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
        }else{ // 웹 반환
            selected = factory.select(Projections.constructor(CommunityPostListResDTO.class,
                    Expressions.numberTemplate(Double.class, "({0} * 0.7 + {1} * 0.3)",
                            likeCommunity.countDistinct().coalesce(0L),
                            communityPost.count.coalesce(0)).as("popularScore"),
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



        List <CommunityPostListResDTO> data = selected.from(communityPost)
                .leftJoin(user).on(communityPost.user.userNo.eq(user.userNo))
                .leftJoin(likeCommunity).on(likeCommunity.communityPost.communityPostNo.eq(communityPost.communityPostNo),
                        likeCommunity.canceledAt.isNull())
                .leftJoin(communityComment).on(communityPost.communityPostNo.eq(communityComment.communityPost.communityPostNo),
                        communityComment.deletedAt.isNull())//댓글 조인
                .leftJoin(communityReply).on(communityComment.commentNo.eq(communityReply.communityComment.commentNo),
                        communityReply.deletedAt.isNull())//답글 조인
                .where(communityPost.deletedAt.isNull(),// 게시글이 삭제 되지 않았고
                        postTypeEq(condition.getPostType()),// 포스트 타입이 같고, 같지 않을 경우 null == 전체 검색
                        lastNoLt(condition.getOrder(), condition.getLastNo()),//마지막 게시글 번호보다 작은 것
                        baseTimeBetween(condition.getOrder(), condition.getBaseTime())//Hot 게시글 시간 조건
                )
                .orderBy(orderEq(condition.getOrder()).toArray(new OrderSpecifier[0]))
                .groupBy(communityPost.communityPostNo,
                        user.userNo)
                .having(lastPopularScore(condition.getOrder(),condition.getPopularScore(),condition.getLastNo()))
                .limit(condition.getPage().getPageSize())
                .fetch();

        Long total = Optional.ofNullable(
                factory.select(communityPost.count())
                        .from(communityPost)
                        .where(communityPost.deletedAt.isNull(),
                                postTypeEq(condition.getPostType()),
                                baseTimeBetween(condition.getOrder(),condition.getBaseTime()))
                        .fetchOne()
        ).orElse(0L);
        return new PageImpl<>(data, condition.getPage(), total);
    }

    /** 인기순위 점수 구하기 좋아요 * 0.7 + 조회수 * 0.3
     * Hot이 아닐 경우 구해지지 않음,
     * 추후 인기순이 나올 경우 이것으로 계산하고 where절의 baseTimeBetween을 최신순으로 바꾸면 사용 가능
     * */
    private BooleanExpression lastPopularScore(String order, Double popularScore, Long lastNo){
        if (!"hot".equalsIgnoreCase(order) || popularScore == null || lastNo == null) {
            return null;
        }

        NumberExpression<Double> currentPopularScore = Expressions.numberTemplate(
                Double.class,
                "({0} * 0.7 + {1} * 0.3)",
                likeCommunity.countDistinct().coalesce(0L),
                communityPost.count.coalesce(0)
        );
        return currentPopularScore.lt(popularScore)
                .or(
                        currentPopularScore.eq(popularScore)
                                .and(communityPost.communityPostNo.lt(lastNo))
                );

    }

    /** 커뮤니티 게시글 핫한 게시글 불러올 때 시간 제어*/
    private BooleanExpression baseTimeBetween(String order, LocalDateTime baseTime){
        if ("hot".equalsIgnoreCase(order)){
            return communityPost.createdAt.between(baseTime.minusDays(3),baseTime);
        }
        // 최신순
        return communityPost.createdAt.loe(baseTime);
    }

    /** 커뮤니티 게시글 Order
     * 핫한 게시물을 뽑아올 때는 3일 이내
     * 좋아요 * 0.7 , 조회수 0.3으로 점수를 매겨 순서를 정해야 함.*/
    private List<OrderSpecifier<?>> orderEq(String order){
        if ("hot".equalsIgnoreCase(order)){
            NumberTemplate<Double> popularScore = Expressions.numberTemplate(Double.class,
                    "({0} * 0.7 + {1} * 0.3)",
                    likeCommunity.countDistinct().coalesce(0L),
                    communityPost.count.coalesce(0)
            );

            return List.of(popularScore.desc(),communityPost.communityPostNo.desc()); // 핫한 게시글 정렬 정렬
        }
        //최신순
        return order != null ? List.of(communityPost.communityPostNo.desc()) : null;
    }

    /** 최신순일 경우에만 이렇게 구하고, HOT 또는 인기순일 경우에는 lastPopularScore()를 따름*/
    private BooleanExpression lastNoLt(String order,Long lastNo){
        if ("recent".equalsIgnoreCase(order) && lastNo != null) return communityPost.communityPostNo.lt(lastNo);
        return null;
    }

    /** 커뮤니티 게시글 타입이 null 일 경우 전체 검색
     * 아닐 경우 해당 게시글 타입 검색
     * */
    private BooleanExpression postTypeEq(PostType postType){
        return postType !=null ? communityPost.postType.eq(postType) : null;
    }

}
