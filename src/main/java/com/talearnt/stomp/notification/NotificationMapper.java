package com.talearnt.stomp.notification;

import com.talearnt.comment.community.entity.CommunityComment;
import com.talearnt.comment.community.response.CommentNotificationDTO;
import com.talearnt.enums.stomp.NotificationType;
import com.talearnt.stomp.notification.entity.Notification;
import com.talearnt.stomp.notification.response.NotificationResDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;


@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationMapper {
    NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

    @Mappings({
            @Mapping(target = "notificationNo", ignore = true),
            @Mapping(source = "comment.senderNo", target = "senderNo"),
            @Mapping(source = "comment.receiverNo", target = "receiverNo"),
            @Mapping(source = "comment.targetNo", target = "targetNo"),
            @Mapping(source = "comment.content", target = "content"),
            @Mapping(target = "notificationType", expression = "java(notificationType)"),
            @Mapping(target = "isRead", constant = "false"),
            @Mapping(target = "unreadCount", constant = "0"),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "deletedAt", ignore = true)
    })
    Notification toNotificationFromComment(CommentNotificationDTO comment, NotificationType notificationType);


    @Mapping(target = "senderNickname", source = "senderNickname")
    @Mapping(target = "talentCodes", ignore = true) // 필요시 매핑
    NotificationResDTO toNotificationResDTOFromCommentNotificationEntity(Notification notification, String senderNickname);


}
