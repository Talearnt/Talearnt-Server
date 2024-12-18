package com.talearnt.admin.agree.entity;

import com.talearnt.user.infomation.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@ToString
public class Agree {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long agreeNo;  // 약관 동의 번호 (Primary Key)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agree_code_id", nullable = false)
    private AgreeCode agreeCode;  // 약관 코드 ID (Foreign Key)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", nullable = false, referencedColumnName = "user_no")
    private User user;  // 사용자 아이디 번호

    @Column(name = "is_agree",nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean agree = false;  // 동의 여부 (0: 미동의, 1: 동의)

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime agreeDate;  // 마지막 동의 날짜 (최초 동의 날짜가 변경될 수 있음)

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 최초 동의 등록일
}
