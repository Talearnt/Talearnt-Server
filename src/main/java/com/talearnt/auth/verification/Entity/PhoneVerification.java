package com.talearnt.auth.verification.Entity;

import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long phoneVerificationNo;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, length = 4)
    private String verificationCode;

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean isPhoneVerified;

    @Column(nullable = true, updatable = false)
    @CreationTimestamp //insert 쿼리가 발생했을 때 현재 시간값 적용
    private LocalDateTime createdAt;

    public void setIsPhoneVerified(boolean b) {
        this.isPhoneVerified = b;
    }

    public boolean getIsPhoneVerified() {
        return this.isPhoneVerified;
    }
}
