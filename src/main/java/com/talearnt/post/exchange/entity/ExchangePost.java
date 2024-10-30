package com.talearnt.post.exchange.entity;

import com.talearnt.enums.ExchangePostStatus;
import com.talearnt.enums.ExchangeType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
public class ExchangePost {
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private long id;

     @Column(nullable = false, length = 30)
     private String userId;

     @ElementCollection
     private List<TalentCategory> giveTalent;

     @ElementCollection
     private List<TalentCategory> receiveTalent;

     @Column(nullable = false, length = 255)
     private String thumbnail;

     @Column(nullable = false, length = 255)
     private String title;

     @Column(columnDefinition = "TEXT", nullable = false)
     private String content;

     @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
     private int count;

     @Enumerated(EnumType.STRING)
     @Column(nullable = false, length = 6)
     private ExchangeType exchangeType;

     @Enumerated(EnumType.STRING)
     @Column(nullable = false, length = 10)
     private ExchangePostStatus status;

     @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
     private boolean badgeRequired;

     @Column(nullable = false)
     private LocalDate endDate;

     @Column(nullable = false,updatable = false)
     @CreationTimestamp
     private LocalDateTime createdAt;

     @UpdateTimestamp
     @Column(nullable = false)
     private LocalDateTime updatedAt;
     private LocalDateTime deletedAt;
}
