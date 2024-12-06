package com.talearnt.post.exchange;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExchangePostMapper {

    ExchangePostMapper INSTANCE = Mappers.getMapper(ExchangePostMapper.class);

}
