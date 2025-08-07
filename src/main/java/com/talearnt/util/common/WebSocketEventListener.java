package com.talearnt.util.common;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Log4j2
@Component
public class WebSocketEventListener {
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        log.info("🔌 WebSocket 연결됨: {}", event.getUser());
    }

    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        log.info("📡 WebSocket 구독됨: {}", event.getUser());
    }

    @EventListener
    public void handleDisconnectEvent(SessionDisconnectEvent event) {
        log.info("❌ WebSocket 연결 종료됨: {}", event.getSessionId());
    }
}
