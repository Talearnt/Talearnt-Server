package com.talearnt.admin.agree.entity;

import com.talearnt.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AgreeContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long agreeContentId;  // 약관 내용 ID (Primary Key)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agree_code_id", nullable = false)
    private AgreeCode agreeCode;  // 약관 코드 (Foreign Key)

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;  // 약관 내용

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 약관 생성 일자

    @UpdateTimestamp
    @Column
    private LocalDateTime updatedAt;  // 약관 수정 일자
}
