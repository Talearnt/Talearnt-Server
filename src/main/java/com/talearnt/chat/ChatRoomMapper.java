package com.talearnt.chat;

import com.talearnt.chat.entity.ChatRoom;
import com.talearnt.enums.chat.RoomMode;
import com.talearnt.post.exchange.entity.ExchangePost;
import com.talearnt.util.jwt.UserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChatRoomMapper {
    ChatRoomMapper INSTANCE = Mappers.getMapper(ChatRoomMapper.class);


    @Mappings({
            @Mapping(source = "exchangePost", target = "exchangePost"),
            @Mapping(source = "userInfo.userNo", target = "owner.userNo"),
            @Mapping(source = "roomMode",target = "roomMode")
    })
    ChatRoom toEntity (ExchangePost exchangePost, UserInfo userInfo, RoomMode roomMode);
}
