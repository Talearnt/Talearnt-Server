package com.talearnt.auth.join;

import com.talearnt.admin.agree.entity.Agree;
import com.talearnt.auth.join.request.AgreeJoinReqDTO;
import com.talearnt.auth.join.request.JoinReqDTO;
import com.talearnt.auth.join.request.KakaoJoinReqDTO;
import com.talearnt.user.infomation.entity.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface JoinMapper {

    JoinMapper INSTANCE = Mappers.getMapper(JoinMapper.class);

    User toEntity(JoinReqDTO joinReqDTO);

    User toUserEntityFromKakaoJoinReqDTO(KakaoJoinReqDTO kakaoJoinReqDTO);
    @Mappings({
            @Mapping(source = "agreeCodeId",target = "agreeCode.agreeCodeId"),
            @Mapping(source = "userNo",target = "user.userNo"),
    })
    Agree toAgreeEntity(AgreeJoinReqDTO agreeReqDTO);
}
