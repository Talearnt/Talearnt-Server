package com.talearnt.user.talent;

import com.talearnt.user.talent.entity.MyTalent;
import com.talearnt.util.jwt.UserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MyTalentMapper {
    MyTalentMapper INSTANCE = Mappers.getMapper(MyTalentMapper.class);


    @Mappings({
            @Mapping(source = "code", target = "talentCategory.talentCode"),
            @Mapping(source = "userInfo.userNo", target = "user.userNo"),
            @Mapping(expression = "java(false)", target = "type")
    })
    MyTalent toGiveEntity(Integer code, UserInfo userInfo);

    @Mappings({
            @Mapping(source = "code", target = "talentCategory.talentCode"),
            @Mapping(source = "userInfo.userNo", target = "user.userNo"),
            @Mapping(expression = "java(true)", target = "type")
    })
    MyTalent toInterestEntity(Integer code, UserInfo userInfo);

    // 리스트 매핑
    default List<MyTalent> toGiveEntities(List<Integer> codes, UserInfo userInfo) {
        return codes.stream()
                .map(code -> toGiveEntity(code, userInfo))
                .collect(Collectors.toList());
    }

    default List<MyTalent> toInterestEntities(List<Integer> codes, UserInfo userInfo) {
        return codes.stream()
                .map(code -> toInterestEntity(code, userInfo))
                .collect(Collectors.toList());
    }
}
