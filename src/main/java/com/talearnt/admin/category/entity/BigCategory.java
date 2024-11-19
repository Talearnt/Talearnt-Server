package com.talearnt.admin.category.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/*
 * 대분류 코드는 Auto_Increment가 아닙니다.
 * IT - 1000 번대
 * 디자인 - 2000 번대
 * 상담 - 3000 번대
 * 등, 코드로 범위를 지정하기 위해 Auto-Increment를 뺐습니다.
 * 대분류 코드는 1000단위입니다.
 * */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BigCategory {
    @Id
    @Column(nullable = false)
    private Integer categoryCode; // 대분류 Code

    @Column(length = 20, unique = true)
    private String categoryName; // 대분류 이름

    @Column(nullable = false)
    private String managerId;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt; // 등록일

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean isActive = true; // 사용 여부

}
