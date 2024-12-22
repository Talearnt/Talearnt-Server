package com.talearnt.post.exchange.response;

import com.talearnt.enums.post.ExchangePostStatus;
import com.talearnt.enums.post.ExchangeType;
import com.talearnt.enums.user.UserRole;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ExchangePostListResDTO {

    private String profileImg;
    private String nickname;
    private boolean authority;

    private ExchangePostStatus status;
    private ExchangeType exchangeType;
    private String duration;
    private boolean requiredBadge;

    private String title;
    private String content;
    private List<String> giveTalents;
    private List<String> receiveTalents;

    private LocalDateTime createdAt;
    private int count;
    private int favoriteCount;

}
