package com.talearnt.stomp.firebase.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.talearnt.user.infomation.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FireBaseCloudMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long firebaseCloudMessageNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", nullable = false)
    private User user;

    @Column(length = 255)
    private String fcmToken;

    @Column(length = 255)
    private String deviceIdentifier;

    @Column(length = 100)
    private String deviceInfo;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
