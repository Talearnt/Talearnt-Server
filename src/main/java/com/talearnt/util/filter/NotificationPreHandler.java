package com.talearnt.util.filter;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.jwt.JwtTokenUtil;
import com.talearnt.util.jwt.UserInfo;
import com.talearnt.util.jwt.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
@Log4j2
public class NotificationPreHandler implements ChannelInterceptor {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserInfoService userInfoService;

    private static final String BEARER_PREFIX = "Bearer ";


    @Override
    public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        log.info("WebSocket STOMP Command: {}", accessor.getCommand());
        // CONNECT 프레임일 때만 토큰 검증
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            log.info("Authorization Header: {}", authHeader);
            if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
                String token = authHeader.substring(7);
                log.info("Token: {}", token);
                if (jwtTokenUtil.isTokenValid(token)) {
                    String userId = jwtTokenUtil.extractUserId(token);
                    UserInfo userInfo = (UserInfo)userInfoService.loadUserByUsername(userId);

                    log.info("Authenticated UserInfo: {}", userInfo);
                    log.info("User: {}", userId);
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userInfo, null, userInfo.getAuthorities()
                    );

                    accessor.setUser(authentication); // → 이후 @AuthenticationPrincipal 에 사용됨
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    log.error("올바르지 않은 인증 토큰 - {} : {}", token,ErrorCode.INVALID_AUTH_CODE);
                    throw new CustomRuntimeException(ErrorCode.INVALID_AUTH_CODE);
                }
            }
        }

        return message;
    }

}
