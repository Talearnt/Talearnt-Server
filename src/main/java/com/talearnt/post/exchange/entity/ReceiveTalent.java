package com.talearnt.post.exchange.entity;

import com.talearnt.admin.category.entity.TalentCategory;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ReceiveTalent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wantReceiveTalentNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchange_post_no")
    private ExchangePost exchangePost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "talent_code")
    private TalentCategory talentCode;
}
