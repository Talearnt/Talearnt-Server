package com.talearnt.login;

import com.talearnt.join.User;
import com.talearnt.util.jwt.UserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoginMapper {

    LoginMapper INSTANCE = Mappers.getMapper(LoginMapper.class);

    @Mappings({
            @Mapping(source = "userNo", target = "userNo"),
            @Mapping(source = "userId", target = "userId"),
            @Mapping(source = "nickname", target = "nickname"),
            @Mapping(source = "profileImg", target = "profileImg"),
            @Mapping(source = "authority", target = "authority"),
    })
    UserInfo toUserInfo(User user);
}
