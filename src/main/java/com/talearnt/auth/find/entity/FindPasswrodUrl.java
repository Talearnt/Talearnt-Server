package com.talearnt.auth.find.entity;

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
public class FindPasswrodUrl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false,length = 40)
    private String uuid;

    @Column(updatable = false,nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
