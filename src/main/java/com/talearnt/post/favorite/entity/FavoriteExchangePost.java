package com.talearnt.post.favorite.entity;


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

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private LocalDateTime deletedAt;

}
