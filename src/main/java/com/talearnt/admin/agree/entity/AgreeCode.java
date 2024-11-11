package com.talearnt.join.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@ToString
public class AgreeCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long agreeCodeId;  // 약관 코드 ID (Primary Key)

    @Column(nullable = false, length = 100)
    private String category;   // 약관 종류 (예: 마케팅, 개인정보 처리방침)

    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;  // 약관 등록일

    @Column(nullable = false, length = 50)
    private String version;  // 약관 버전 (예: "1.0", "2.0")

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
    private boolean isActive;  // 약관 사용 여부 (true: 사용, false: 미사용)

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
    private boolean isMandatory;  // 필수 여부 (true: 필수, false: 선택)



}
