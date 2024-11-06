package com.talearnt.post.exchange;

import com.talearnt.post.exchange.entity.ExchangePost;
import com.talearnt.post.exchange.response.ExchangePostReadResDTO;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ExchangePostRepository extends JpaRepository<ExchangePost, Long> {
    Optional<ExchangePost> findByExchangePostNoAndDeletedAtIsNull(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("DELETE FROM ExchangePost ep WHERE ep.exchangePostNo = :exchangePostNo AND ep.user.userNo = :userNo")
    int deleteByPostIdAndUserNo(@Param("exchangePostNo") Long exchangePostNo, @Param("userNo") long userNo);
}
