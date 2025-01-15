package com.talearnt.chat.entity;

import com.talearnt.enums.chat.ChatRole;
import com.talearnt.user.infomation.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChatParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatParticipantNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_request_no", nullable = false)
    private ChatRequest chatRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", nullable = false)
    private User participantUserNo;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatRole role;
}
