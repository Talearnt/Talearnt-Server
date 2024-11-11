package com.talearnt.post.exchange.entity;

import com.talearnt.user.entity.MyTalent;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class GiveTalent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wantGiveTalentNo; // 주고 싶은 재능 번호 (auto_increment)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "my_talent_no")
    private MyTalent myTalentNo; // 나의 재능 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchange_post_no")
    private ExchangePost exchangePostNo; // 게시글 번호
}
