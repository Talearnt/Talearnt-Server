package com.talearnt.post.exchange;

import com.talearnt.post.exchange.entity.ExchangePost;
import com.talearnt.post.exchange.request.ExchangePostReqDTO;
import com.talearnt.post.exchange.response.ExchangePostReadResDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExchangePostMapper {
    ExchangePostMapper INSTANCE = Mappers.getMapper(ExchangePostMapper.class);

    @Mappings({
            @Mapping(source = "user.userNo", target = "userNo"),
            @Mapping(source = "user.userId", target = "userId"),
            @Mapping(source = "user.nickname", target = "nickname"),
            @Mapping(source = "user.profileImg", target = "profileImg"),
            @Mapping(source = "user.authority", target = "authority")
    })
    ExchangePostReadResDTO toExchangePostReadResDTO(ExchangePost exchangePost);

    @Mapping(source = "userInfo", target = "user")
    ExchangePost toEntity(ExchangePostReqDTO exchangePostReqDTO);

    @Mapping(source = "userInfo", target = "user")
    ExchangePost toUpdateEntity(ExchangePostReqDTO exchangePostReqDTO, @MappingTarget ExchangePost exchangePost);
}
