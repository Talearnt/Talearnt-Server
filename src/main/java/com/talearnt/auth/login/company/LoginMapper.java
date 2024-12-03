package com.talearnt.auth.login.company;

import com.talearnt.user.infomation.entity.User;
import com.talearnt.util.jwt.UserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoginMapper {

    LoginMapper INSTANCE = Mappers.getMapper(LoginMapper.class);

    UserInfo toUserInfo(User user);
}
