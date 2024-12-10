package com.talearnt.post.exchange.entity;

import com.talearnt.enums.post.ExchangePostStatus;
import com.talearnt.enums.post.ExchangeType;
import com.talearnt.user.infomation.entity.User;
import com.talearnt.util.converter.post.ExchangePostStatusConverter;
import com.talearnt.util.converter.post.ExchangeTypeConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
public class ExchangePost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exchangePostNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no") // 내일 User Entity id 를 userNo로 변경 요청
    private User user;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @OneToMany(mappedBy = "exchangePost", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, orphanRemoval = true)
    @BatchSize(size = 5) // 한 번에 5개의 연관 엔티티를 가져옴
    private List<GiveTalent> giveTalents;

    @OneToMany(mappedBy = "exchangePost", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, orphanRemoval = true)
    @BatchSize(size = 5)
    private List<ReceiveTalent> receiveTalents;


    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int count;


    @Enumerated(EnumType.STRING)
    @Convert(converter = ExchangeTypeConverter.class)
    @Column(nullable = false, length = 6)
    private ExchangeType exchangeType;

    @Enumerated(EnumType.STRING)
    @Convert(converter = ExchangePostStatusConverter.class)
    @Column(nullable = false, length = 10, columnDefinition = "VARCHAR(10) DEFAULT '모집중'")
    private ExchangePostStatus status;

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean badgeRequired;

    @Column(nullable = false)
    private String duration;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;


    @PrePersist
    public void prePersist() {

        if (this.status == null) {
            this.status = ExchangePostStatus.모집중;
        }
    }
}
