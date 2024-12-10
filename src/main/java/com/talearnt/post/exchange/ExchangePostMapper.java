package com.talearnt.post.exchange;

import com.talearnt.admin.category.entity.TalentCategory;
import com.talearnt.post.exchange.entity.ExchangePost;
import com.talearnt.post.exchange.entity.GiveTalent;
import com.talearnt.post.exchange.entity.ReceiveTalent;
import com.talearnt.post.exchange.request.ExchangePostReqDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExchangePostMapper {

    ExchangePostMapper INSTANCE = Mappers.getMapper(ExchangePostMapper.class);

    @Mapping(target = "giveTalents", ignore = true) // 커스텀 매핑 필요
    @Mapping(target = "receiveTalents", ignore = true) // 커스텀 매핑 필요
    ExchangePost toExchangePostEntity(ExchangePostReqDTO dto);

    @AfterMapping
    default void mappingExchangePost(ExchangePostReqDTO dto, @MappingTarget ExchangePost exchangePost) {
        // GiveTalents 매핑
        List<GiveTalent> giveTalents = dto.getGiveTalents().stream()
                .map(talentCode -> {
                    GiveTalent giveTalent = new GiveTalent();
                    giveTalent.setTalentCode(new TalentCategory(talentCode)); // TalentCategory를 코드로 설정
                    giveTalent.setExchangePost(exchangePost); // 연관관계 매핑
                    return giveTalent;
                })
                .toList();
        exchangePost.setGiveTalents(giveTalents);

        // ReceiveTalents 매핑
        List<ReceiveTalent> receiveTalents = dto.getReceiveTalents().stream()
                .map(talentCode -> {
                    ReceiveTalent receiveTalent = new ReceiveTalent();
                    receiveTalent.setTalentCode(new TalentCategory(talentCode)); // TalentCategory를 코드로 설정
                    receiveTalent.setExchangePost(exchangePost); // 연관관계 매핑
                    return receiveTalent;
                })
                .toList();
        exchangePost.setReceiveTalents(receiveTalents);
    }

}
