package com.talearnt.admin.agree.entity;

import com.talearnt.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String title;   // 약관 이름 (예: 마케팅, 개인정보 처리방침)

    @Column(nullable = false, length = 50)
    private String version;  // 약관 버전 (예: "1.0", "2.0")

    @Column(name = "is_active", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
    private boolean active = true;  // 약관 사용 여부 (true: 사용, false: 미사용)

    @Column(name = "is_mandatory", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
    private boolean mandatory = true;  // 필수 여부 (true: 필수, false: 선택)

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 약관 등록일
}
