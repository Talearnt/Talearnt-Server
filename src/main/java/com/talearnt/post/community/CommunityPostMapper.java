package com.talearnt.post.community;

import com.talearnt.post.community.entity.CommunityPost;
import com.talearnt.post.community.request.CommunityPostReqDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommunityPostMapper {

    CommunityPostMapper INSTANCE = Mappers.getMapper(CommunityPostMapper.class);

    @Mapping(source = "userInfo.userNo",target = "user.userNo")
    CommunityPost toEntity(CommunityPostReqDTO reqDTO);

}
