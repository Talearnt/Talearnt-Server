package com.talearnt.join;

import com.talearnt.admin.agree.entity.Agree;
import com.talearnt.join.request.AgreeJoinReqDTO;
import com.talearnt.join.request.JoinReqDTO;
import com.talearnt.user.entity.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface JoinMapper {

    JoinMapper INSTANCE = Mappers.getMapper(JoinMapper.class);

    User toEntity(JoinReqDTO joinReqDTO);

    @Mappings({
            @Mapping(source = "agreeCodeId",target = "agreeCode.agreeCodeId"),
            @Mapping(source = "userNo",target = "user.userNo"),
    })
    Agree toAgreeEntity(AgreeJoinReqDTO agreeReqDTO);
}
