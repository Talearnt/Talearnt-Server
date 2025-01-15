package com.talearnt.chat.entity;

import com.talearnt.enums.chat.MessageType;
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
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_request_no", nullable = false)
    private ChatRequest chatRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", nullable = false)
    private User senderNo;

    @Column(name = "message", nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime deletedAt;

}
