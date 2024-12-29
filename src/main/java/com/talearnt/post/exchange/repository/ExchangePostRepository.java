package com.talearnt.post.exchange.repository;

import com.talearnt.post.exchange.entity.ExchangePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExchangePostRepository extends JpaRepository<ExchangePost, Long> {}
