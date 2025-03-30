package com.talearnt.comment.community;

import com.talearnt.comment.community.entity.CommunityComment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);
    @Mappings({
            @Mapping(source = "userNo", target = "user.userNo"),
            @Mapping(source = "communityPostNo", target = "communityPost.communityPostNo"),
            @Mapping(source = "content", target = "content")
    })
    CommunityComment toEntity(Long userNo, Long communityPostNo, String content);

}
