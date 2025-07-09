package com.talearnt.reply.community;

import com.talearnt.reply.community.entity.CommunityReply;
import com.talearnt.user.infomation.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReplyMapper {

    ReplyMapper INSTANCE = Mappers.getMapper(ReplyMapper.class);

    @Mappings({
            @Mapping(source = "user", target = "user"),
            @Mapping(source = "commentNo", target = "communityComment.commentNo"),
            @Mapping(source = "content", target = "content")
    })
    CommunityReply toEntity(User user, Long commentNo, String content);
}
