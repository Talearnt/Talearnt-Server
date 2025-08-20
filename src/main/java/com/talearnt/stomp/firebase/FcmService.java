package com.talearnt.stomp.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.talearnt.enums.common.ErrorCode;
import com.talearnt.stomp.firebase.entity.FireBaseCloudMessage;
import com.talearnt.stomp.firebase.repository.FcmTokenQueryRepository;
import com.talearnt.stomp.firebase.repository.FcmTokenRepository;
import com.talearnt.user.infomation.entity.User;
import com.talearnt.user.infomation.repository.UserRepository;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.log.LogRunningTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class FcmService {
    
    private final FcmTokenRepository fcmTokenRepository;
    private final FcmTokenQueryRepository fcmTokenQueryRepository;
    private final UserRepository userRepository;
    
    /**
     * FCM 토큰 저장
     * 조건)
     * - 로그인이 되어 있어야 한다. (Binder에서 해결)
     * - FCM 토큰이 유효해야 한다.
     * - 디바이스별로 하나의 토큰만 유지한다.
     */
    @LogRunningTime
    public Long saveFcmToken(Long userNo, String fcmToken, String deviceIdentifier, String deviceInfo) {
        log.info("FCM 토큰 저장 시작: userNo={}, deviceIdentifier={}", userNo, deviceIdentifier);
        
        User user = userRepository.findByUserNo(userNo)
                .orElseThrow(() -> new CustomRuntimeException(ErrorCode.USER_NOT_FOUND));
        
        // 기존 토큰이 있는지 확인
        FireBaseCloudMessage existingToken = fcmTokenQueryRepository
                .findByUserNoAndDeviceIdentifier(userNo, deviceIdentifier)
                .orElse(null);
        
        if (existingToken != null) {
            // 기존 토큰 업데이트
            existingToken.setFcmToken(fcmToken);
            existingToken.setDeviceInfo(deviceInfo);
            existingToken.setUpdatedAt(LocalDateTime.now());
            fcmTokenRepository.save(existingToken);
            log.info("FCM 토큰 업데이트 완료: userNo={}, token={}", userNo, fcmToken);
            return existingToken.getFirebaseCloudMessageNo();
        } else {
            // 새 토큰 생성
            FireBaseCloudMessage newToken = new FireBaseCloudMessage();
            newToken.setUser(user);
            newToken.setFcmToken(fcmToken);
            newToken.setDeviceIdentifier(deviceIdentifier);
            newToken.setDeviceInfo(deviceInfo);
            newToken.setUpdatedAt(LocalDateTime.now());
            
            FireBaseCloudMessage savedToken = fcmTokenRepository.save(newToken);
            log.info("새 FCM 토큰 저장 완료: userNo={}, token={}", userNo, fcmToken);
            return savedToken.getFirebaseCloudMessageNo();
        }
    }
    
    /**
     * 특정 사용자에게 FCM 메시지 전송
     * 조건)
     * - 사용자의 모든 활성 FCM 토큰으로 메시지 전송
     * - 유효하지 않은 토큰은 자동으로 삭제
     */
    @LogRunningTime
    public void sendMessageToUser(Long userNo, String title, String body, Map<String, String> data) {
        log.info("사용자 {}에게 FCM 메시지 전송 시작: title={}", userNo, title);
        
        List<FireBaseCloudMessage> tokens = fcmTokenQueryRepository.findActiveTokensByUserNo(userNo);
        
        if (tokens.isEmpty()) {
            log.warn("사용자 {}의 FCM 토큰이 없습니다.", userNo);
            return;
        }
        
        List<String> validTokens = new ArrayList<>();
        List<String> invalidTokens = new ArrayList<>();
        
        // 각 토큰으로 메시지 전송 시도
        for (FireBaseCloudMessage token : tokens) {
            try {
                sendMessageToToken(token.getFcmToken(), title, body, data);
                validTokens.add(token.getFcmToken());
                log.debug("FCM 메시지 전송 성공: token={}", token.getFcmToken());
            } catch (FirebaseMessagingException e) {
                log.warn("FCM 메시지 전송 실패: token={}, error={}", token.getFcmToken(), e.getMessage());
                invalidTokens.add(token.getFcmToken());
            }
        }
        
        // 유효하지 않은 토큰들 삭제
        if (!invalidTokens.isEmpty()) {
            removeInvalidTokens(invalidTokens);
            log.info("유효하지 않은 FCM 토큰 {}개 삭제 완료", invalidTokens.size());
        }
        
        log.info("FCM 메시지 전송 완료: userNo={}, 성공={}, 실패={}", 
                userNo, validTokens.size(), invalidTokens.size());
    }
    
    /**
     * 특정 FCM 토큰으로 메시지 전송
     */
    private void sendMessageToToken(String token, String title, String body, Map<String, String> data) throws FirebaseMessagingException{
        Message.Builder messageBuilder = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                       .setTitle(title)
                       .setBody(body)
                       .build());
        
        //추가 데이터 설정
        if (data != null && !data.isEmpty()) {
            messageBuilder.putAllData(data);
        }
        
        Message message = messageBuilder.build();
        FirebaseMessaging.getInstance().send(message);
    }
    
    /**
     * 유효하지 않은 FCM 토큰들 삭제
     */
    private void removeInvalidTokens(List<String> invalidTokens) {
        fcmTokenRepository.deleteByFcmTokens(invalidTokens);
    }
    
    /**
     * 사용자의 모든 FCM 토큰 삭제
     * 조건)
     * - 사용자 탈퇴 시 호출
     * - 로그아웃 시 호출
     */
    @LogRunningTime
    public void removeAllUserTokens(Long userNo) {
        log.info("사용자 {}의 모든 FCM 토큰 삭제 시작", userNo);
        fcmTokenRepository.deleteByUserNo(userNo);
        log.info("사용자 {}의 모든 FCM 토큰 삭제 완료", userNo);
    }
    
    /**
     * 특정 FCM 토큰 삭제
     * 조건)
     * - 토큰이 유효하지 않을 때 호출
     */
    @LogRunningTime
    public void removeToken(String fcmToken) {
        log.info("FCM 토큰 삭제 시작: {}", fcmToken);
        fcmTokenRepository.deleteByFcmToken(fcmToken);
        log.info("FCM 토큰 삭제 완료: {}", fcmToken);
    }
    
    /**
     * 사용자의 FCM 토큰 개수 조회
     */
    public long getUserTokenCount(Long userNo) {
        return fcmTokenQueryRepository.countTokensByUserNo(userNo);
    }
    
}
