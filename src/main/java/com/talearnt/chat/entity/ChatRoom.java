package com.talearnt.chat.entity;


import com.talearnt.enums.chat.RoomMode;
import com.talearnt.post.exchange.entity.ExchangePost;
import com.talearnt.user.infomation.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchange_post_no", nullable = false)
    private ExchangePost exchangePostNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", nullable = false)
    private User ownerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomMode roomMode;

}
