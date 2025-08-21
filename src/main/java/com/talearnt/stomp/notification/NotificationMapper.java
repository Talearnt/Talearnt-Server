package com.talearnt.stomp.notification;


import com.talearnt.comment.community.response.CommentNotificationDTO;
import com.talearnt.enums.stomp.NotificationType;
import com.talearnt.stomp.notification.entity.Notification;
import com.talearnt.stomp.notification.response.NotificationResDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;


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
            @Mapping(target = "talentCodes", ignore = true), // 필요시 매핑
            @Mapping(target = "unreadCount", ignore = true),
            @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    })
    Notification toNotificationFromComment(CommentNotificationDTO comment, NotificationType notificationType);

    @Mappings({
            @Mapping(target = "notificationNo", ignore = true),
            @Mapping(source = "senderNo", target = "senderNo"),
            @Mapping(source = "receiverNo", target = "receiverNo"),
            @Mapping(source = "targetNo", target = "targetNo"),
            @Mapping(source = "postTitle", target="content"),
            @Mapping(source = "notificationType", target = "notificationType"),
            @Mapping(target = "isRead", constant = "false"),
            @Mapping(source = "talentCodes", target = "talentCodes"), // 필요시 매핑
            @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    })
    Notification toNotificationFromExchangePost(Long senderNo, Long receiverNo, Long targetNo, String postTitle, List<Integer> talentCodes, NotificationType notificationType);

    @Mapping(target = "senderNickname", source = "senderNickname")
    @Mapping(target = "talentCodes", expression = "java(new java.util.ArrayList<Integer>())") // 빈 리스트로 매핑
    NotificationResDTO toNotificationResDTOFromCommentNotificationEntity(Notification notification, String senderNickname);

    @Mappings({
            @Mapping(source = "notification.notificationNo", target = "notificationNo"),
            @Mapping(source = "notification.targetNo", target = "targetNo"),
            @Mapping(source = "notification.content", target = "content"),
            @Mapping(source = "notification.notificationType", target = "notificationType"),
            @Mapping(source = "notification.isRead", target = "isRead"),
            @Mapping(source = "notification.createdAt", target = "createdAt"),
            @Mapping(source = "senderNickname", target = "senderNickname"),
            @Mapping(source = "talentCodes",target = "talentCodes")
    })
    NotificationResDTO toNotificationResDTO(Notification notification, List<Integer> talentCodes, String senderNickname);



}
