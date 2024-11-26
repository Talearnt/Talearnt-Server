package com.talearnt.auth.verification.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PhoneVerificationTrace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long phoneVerificationTraceNo;

    @Column(nullable = false)
    private Long phoneVerificationNo;

    @Column(nullable = false,length = 4)
    private String code;

    @Column(length = 20)
    private String phone;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
