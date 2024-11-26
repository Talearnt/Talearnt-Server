package com.talearnt.auth.find;

import com.talearnt.auth.find.entity.FindPasswrodUrl;
import com.talearnt.auth.find.reponse.AuthFindResDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthFindMapper {
    AuthFindMapper INSTANCE = Mappers.getMapper(AuthFindMapper.class);

    /** FindPasswordUrl의 ID와 uuid를 채워주는 Mapper
     * @param userId 회원 아이디
     * @param uuid 고유한 값*/
    @Mappings({
            @Mapping(source = "userId", target = "userId"),
            @Mapping(source = "uuid", target = "uuid")
    })
    FindPasswrodUrl toFindPasswordUrlEntity(String userId, String uuid);

    AuthFindResDTO toUserFindResDTO(FindPasswrodUrl findPasswrodUrl);
}
