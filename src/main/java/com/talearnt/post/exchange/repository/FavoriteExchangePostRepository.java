package com.talearnt.post.exchange.repository;

import com.talearnt.post.exchange.entity.FavoriteExchangePost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteExchangePostRepository extends JpaRepository<FavoriteExchangePost, Long> {
}
