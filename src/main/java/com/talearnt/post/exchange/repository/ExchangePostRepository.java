package com.talearnt.post.exchange.repository;

import com.talearnt.post.exchange.entity.ExchangePost;
import com.talearnt.post.exchange.response.TestListDTO;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExchangePostRepository extends JpaRepository<ExchangePost, Long> {

    @Query(nativeQuery = true,
            value = "SELECT JSON_ARRAYAGG(gtc.talent_name) as giveTalents, JSON_ARRAYAGG(rtc.talent_name) as receiveTalents " +
                    "FROM exchange_post e " +
                    "LEFT JOIN give_talent gt ON gt.exchange_post_no = e.exchange_post_no " +
                    "LEFT JOIN receive_talent rt ON rt.exchange_post_no = e.exchange_post_no " +
                    "LEFT JOIN talent_category gtc ON gtc.talent_code = gt.talent_code " +
                    "LEFT JOIN talent_category rtc ON rtc.talent_code = rt.talent_code")
    List<TestListDTO> getTestList();

}
