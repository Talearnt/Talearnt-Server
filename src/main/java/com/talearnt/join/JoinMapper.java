package com.talearnt.join;

import com.talearnt.user.entity.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface JoinMapper {

    JoinMapper INSTANCE = Mappers.getMapper(JoinMapper.class);

    User toEntity(JoinReqDTO joinReqDTO);

}
