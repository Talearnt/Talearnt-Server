package com.talearnt.user;

import com.talearnt.user.entity.FindPasswrodUrl;
import com.talearnt.user.reponse.UserFindResDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    /** FindPasswordUrl의 ID와 uuid를 채워주는 Mapper
     * @param userId 회원 아이디
     * @param uuid 고유한 값*/
    @Mappings({
            @Mapping(source = "userId", target = "userId"),
            @Mapping(source = "uuid", target = "uuid")
    })
    FindPasswrodUrl toFindPasswordUrlEntity(String userId, String uuid);


    UserFindResDTO toUserFindResDTO(FindPasswrodUrl findPasswrodUrl);
}
