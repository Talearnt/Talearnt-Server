package com.talearnt.post.favorite.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.talearnt.post.favorite.entity.FavoriteExchangePost;
import com.talearnt.post.favorite.entity.QFavoriteExchangePost;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Log4j2
public class FavoriteExchagePostQueryRepository {
    private final JPAQueryFactory factory;
    private final QFavoriteExchangePost favoriteExchangePost = QFavoriteExchangePost.favoriteExchangePost;

    public Optional<FavoriteExchangePost> findByPostNoAndUserId(Long postNo, Long userNo) {
        return Optional.ofNullable(
                factory.selectFrom(favoriteExchangePost)
                        .where(favoriteExchangePost.exchangePostNo.eq(postNo),
                                favoriteExchangePost.userNo.eq(userNo))
                        .fetchOne()
        );
    }

}
