package com.talearnt.reply.community;

import com.talearnt.reply.community.entity.CommunityReply;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReplyMapper {

    ReplyMapper INSTANCE = Mappers.getMapper(ReplyMapper.class);

    @Mappings({
            @Mapping(source = "userNo", target = "user.userNo"),
            @Mapping(source = "commentNo", target = "communityComment.commentNo"),
            @Mapping(source = "content", target = "content")
    })
    CommunityReply toEntity(Long userNo, Long commentNo, String content);
}
