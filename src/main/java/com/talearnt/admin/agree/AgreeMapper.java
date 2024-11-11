package com.talearnt.admin.agree;

import com.talearnt.admin.agree.entity.AgreeCode;
import com.talearnt.admin.agree.entity.AgreeContent;
import com.talearnt.admin.agree.request.AgreeCodeReqDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AgreeMapper {

    AgreeMapper INSTANCE = Mappers.getMapper(AgreeMapper.class);


    @Mapping(source = "userInfo", target = "user")
    AgreeCode toAgreeCodeEntity(AgreeCodeReqDTO agreeCodeReqDTO);

    @Mapping(source = "userInfo", target = "user")
    AgreeContent toAgreeContentEntity(AgreeCodeReqDTO agreeCodeReqDTO);
}
