package com.talearnt.post.exchange.entity;


import com.talearnt.user.infomation.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteExchangePost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long favoriteExchangePostNo;

    @Column(nullable = false)
    private Long exchangePostNo;

    @Column(nullable = false)
    private Long userNo;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private LocalDateTime deletedAt;

}
