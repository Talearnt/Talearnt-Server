package com.talearnt.configure;


import com.talearnt.util.filter.NotificationPreHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Log4j2
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketStompConfig implements WebSocketMessageBrokerConfigurer {

    private final NotificationPreHandler notificationPreHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        /* 메세지 구독(수신) : 접두사로 시작하는 메세지
            * 예시) /sub/post/1
            *
            * 클라이언트에서 메세지를 구독할 때는 "/sub" 접두사를 사용합니다.
            * 이 접두사는 서버가 메세지를 구독(수신)하는 경로를 나타냅니다.
            *
            * 예시) @SubscribeMapping("/post/{id}")
            *      -> 클라이언트에서 "/sub/post/1"로 메세지를 구독하면 이 메소드가 호출됩니다.
         */
        log.info("/sub/ 접두사로 시작하는 메세지 구독 설정");
        registry.enableSimpleBroker("/sub");

        /* 메세지 전송(발행) : 접두사로 시작하는 메세지
            * 예시) /pub/post/1
            *
            * 클라이언트에서 메세지를 전송할 때는 "/pub" 접두사를 사용합니다.
            * 이 접두사는 서버가 메세지를 수신하는 경로를 나타냅니다.
            *
            * 예시) @MessageMapping("/post/{id}")
            *      -> 클라이언트에서 "/pub/post/1"로 메세지를 전송하면 이 메소드가 호출됩니다.
        * */
        log.info("/pub/ 접두사로 시작하는 메세지 전송 설정");
        registry.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(notificationPreHandler);
    }

}
