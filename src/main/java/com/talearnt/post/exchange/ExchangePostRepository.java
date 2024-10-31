package com.talearnt.post.exchange;

import com.talearnt.post.exchange.entity.ExchangePost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangePostRepository extends JpaRepository<ExchangePost,Long> {

}
