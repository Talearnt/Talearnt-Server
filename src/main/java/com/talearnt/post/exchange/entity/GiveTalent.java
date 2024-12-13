package com.talearnt.post.exchange.entity;

import com.talearnt.admin.category.entity.TalentCategory;
import com.talearnt.user.talent.entity.MyTalent;
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
    @JoinColumn(name = "exchange_post_no")
    @ToString.Exclude
    private ExchangePost exchangePost; // 게시글 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "talent_code")
    private TalentCategory talentCode;
}
