package com.talearnt.post.community.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.talearnt.post.community.entity.LikeCommunity;
import com.talearnt.post.community.entity.QLikeCommunity;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Log4j2
@RequiredArgsConstructor
public class LikeCommunityQueryRepository {

    private final JPAQueryFactory factory;
    private final QLikeCommunity likeCommunity = QLikeCommunity.likeCommunity;


    public Optional<LikeCommunity> findByPostNoAndUserNo(Long postNo, Long userNo) {
        return Optional.ofNullable(
                factory.selectFrom(likeCommunity)
                        .where(likeCommunity.communityPost.communityPostNo.eq(postNo),
                                likeCommunity.userNo.eq(userNo))
                        .fetchOne()
        );
    }

}
