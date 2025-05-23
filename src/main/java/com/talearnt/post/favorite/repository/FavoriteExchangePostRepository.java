package com.talearnt.post.favorite.repository;

import com.talearnt.post.favorite.entity.FavoriteExchangePost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteExchangePostRepository extends JpaRepository<FavoriteExchangePost, Long> {
}
