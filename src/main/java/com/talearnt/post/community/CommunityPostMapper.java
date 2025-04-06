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


    @Mappings({
            @Mapping(source = "reqDTO.userInfo.userNo", target = "user.userNo"),
            @Mapping(source = "postNo", target = "communityPostNo"),
            @Mapping(source = "reqDTO.title", target = "title"),
            @Mapping(source = "reqDTO.content", target = "content"),
            @Mapping(source = "reqDTO.postType", target = "postType")
    })
    CommunityPost updateEntity(Long postNo,CommunityPostReqDTO reqDTO);


}
