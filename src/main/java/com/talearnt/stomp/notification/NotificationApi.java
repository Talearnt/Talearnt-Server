package com.talearnt.stomp.notification;

import com.talearnt.stomp.notification.response.NotificationResDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public interface NotificationApi {



    @Operation(summary = "게시글에 작성된 댓글 알림 전송",
            tags = "Notification",
            description = "<h2>내용</h2>" +
                    "<p>WebSocket을 통한 실시간 알림 시스템입니다.</p>" +
                    "<p>JWT 토큰을 사용하기 때문에 Bearer 방식으로 Header 에 넣어주세요!</p>" +
                    "<p>댓글, 좋아요 등의 이벤트가 발생할 때 해당 사용자에게 실시간으로 알림을 전달합니다.</p>" +
                    "<h2>WebSocket 연결 설정</h2>" +
                    "<ul>" +
                        "<li>연결 엔드포인트: /ws</li>" +
                        "<li>구독 경로: /user/queue/notifications</li>" +
                    "</ul>" +
                    "<h2>수신되는 메시지 형식</h2>" +
                    "<pre>" +
                    "{\n" +
                    "  \"notificationNo\": 6,\n" +
                    "  \"senderNickname\": \"360개월신생아\",\n" +
                    "  \"targetNo\": 33,\n" +
                    "  \"content\": \"오는 방식이 좀 색다른데?\",\n" +
                    "  \"notificationType\": \"댓글\",\n" +
                    "  \"talentCodes\": null,\n" +
                    "  \"isRead\": false,\n" +
                    "  \"unreadCount\": 0,\n" +
                    "  \"createdAt\": \"2025-08-08T00:35:52.753409\"\n" +
                    "}" +
                    "</pre>" +
                    "<h2>Response 필드 설명</h2>" +
                    "<ul>" +
                        "<li>notificationNo : 알림 고유 번호</li>" +
                        "<li>senderNickname : 알림을 발생시킨 사용자의 닉네임</li>" +
                        "<li>targetNo : 알림 관련 대상 ID (예: 게시글 번호)</li>" +
                        "<li>content : 알림 내용 메시지</li>" +
                        "<li>notificationType : 알림 유형 (댓글, 맞춤 키워드 등)</li>" +
                        "<li>talentCodes : 재능 코드 정보 (필요시 사용)</li>" +
                        "<li>isRead : 알림 읽음 여부</li>" +
                        "<li>unreadCount : 읽지 않은 알림 수</li>" +
                        "<li>createdAt : 알림 생성 시간</li>" +
                    "</ul>" +
                    "<h2>주의사항</h2>" +
                    "<ul>" +
                        "<li>WebSocket 연결 시 유효한 JWT 토큰이 필요합니다. 헤더에 넣어주세요.</li>" +
                        "<li>인증된 사용자만 알림을 구독하고 수신할 수 있습니다.</li>" +
                        "<li>네트워크 연결이 끊어질 경우 자동 재연결 로직 구현을 권장합니다.</li>" +
                    "</ul>"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 전송 성공",
                    content = @Content(schema = @Schema(implementation = NotificationResDTO.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public NotificationResDTO dummyNotification(NotificationResDTO notification);
}
