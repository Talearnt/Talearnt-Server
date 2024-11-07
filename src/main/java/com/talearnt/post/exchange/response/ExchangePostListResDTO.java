package com.talearnt.post.exchange.response;

import com.talearnt.enums.UserRole;
import com.talearnt.enums.post.ExchangePostStatus;
import com.talearnt.post.exchange.PostTalentCategoryDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ExchangePostListResDTO {
    private long exchangePostNo;
    private String nickname;
    private UserRole authority;
    private List<PostTalentCategoryDTO> giveTalent;
    private List<PostTalentCategoryDTO> receiveTalent;
    private String title;
    private String content;
    private ExchangePostStatus status;
    private LocalDateTime createdAt;
}
