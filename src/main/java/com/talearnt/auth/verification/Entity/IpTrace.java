package com.talearnt.auth.verification.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
public class IpTrace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ipNo;

    @Column(nullable = false, length = 45)
    private String ip;

    @Column(nullable = false)
    private int requestCount;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime lastRequestTime;
}
