package com.talearnt.post.exchange;

import com.talearnt.post.exchange.entity.ExchangePost;
import com.talearnt.post.exchange.request.ExchangePostReqDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;


@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExchangePostMapper {

    ExchangePostMapper INSTANCE = Mappers.getMapper(ExchangePostMapper.class);

    @Mapping(source = "userInfo.userNo",target = "user.userNo")
    @Mapping(target = "giveTalents", ignore = true) // 매핑 생략
    @Mapping(target = "receiveTalents", ignore = true) // 매핑 생략
    ExchangePost toExchangePostEntity(ExchangePostReqDTO dto);

}
