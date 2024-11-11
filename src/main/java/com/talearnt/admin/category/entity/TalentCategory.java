package com.talearnt.admin.category.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TalentCategory {

    @Id
    private Integer talentCode; // 재능 Code

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_code")
    private BigCategory categoryCode; // 대분류 Code

    @Column(length = 20, nullable = false)
    private String talentName; // 재능 이름

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt; // 등록일

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean isActive; // 사용 여부
}
