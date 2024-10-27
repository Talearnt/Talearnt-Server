package com.talearnt.verification;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class PhoneVerification {

    @Id
    @Column(nullable = false)
    private String userId;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, length = 4)
    private String verificationCode;

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isPhoneVerified;

    @Column(nullable = true, updatable = false)
    @CreationTimestamp //insert 쿼리가 발생했을 때 현재 시간값 적용
    private LocalDateTime createdAt;

    @Builder
    public PhoneVerification(String userId, String phone, String verificationCode, Boolean isPhoneVerified) {
        this.userId = userId;
        this.phone = phone;
        this.verificationCode = verificationCode;
        this.isPhoneVerified = isPhoneVerified;
    }
}
