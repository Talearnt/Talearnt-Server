package com.talearnt.post.exchange.request;

import com.talearnt.enums.post.ExchangePostStatus;
import com.talearnt.enums.post.ExchangeType;
import com.talearnt.util.common.PostUtil;
import com.talearnt.util.common.SplitUtil;
import lombok.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
@NoArgsConstructor
@ToString
public class ExchangeSearchConditionDTO {
    private List<Integer> giveTalents; //Integer로 변환 필요
    private List<Integer> receiveTalents; //Integer로 변환 필요
    private String order; //recent,popular 로 변환 필요
    private String duration; // 이상한 값이 넘어올 경우 duration 없이 조건
    private ExchangeType type; //ExchangeType으로 변환 필요, ExchangeType 으로 변환 실패 시 null로 변환
    private Boolean requiredBadge; // Boolean 값으로 넘어오지 않을 경우 null로 변환
    private ExchangePostStatus status; //ExchangePostStatus으로 변환 필요, ExchangePostStatus 으로 변환 실패시  으로 변환 실패 시 null로 변환
    private Long lastNo;
    private Long firstNo;
    private Pageable page;

    @Builder
    public ExchangeSearchConditionDTO(List<String> giveTalents, List<String> receiveTalents, String order, String duration, String type, String requiredBadge, String status, String page, String size, String lastNo, String firstNo) {
        this.giveTalents = PostUtil.filterValidIntegers(giveTalents);
        this.receiveTalents = PostUtil.filterValidIntegers(receiveTalents);
        this.order = PostUtil.filterValidOrderValue(order);
        this.duration = PostUtil.filterValidDurationValue(duration);
        this.type = PostUtil.filterValidExchangeType(type);
        this.requiredBadge = PostUtil.filterValidRequiredBadge(requiredBadge);
        this.status = PostUtil.filterValidExchangePostStatus(status);
        this.lastNo = PostUtil.parseLong(lastNo);
        this.firstNo = PostUtil.parseLong(firstNo);
        this.page = PostUtil.filterValidPagination(page,size);
    }
}
