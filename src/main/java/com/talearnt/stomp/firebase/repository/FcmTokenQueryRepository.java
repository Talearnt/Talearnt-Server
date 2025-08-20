package com.talearnt.stomp.firebase.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.talearnt.stomp.firebase.entity.FireBaseCloudMessage;
import com.talearnt.stomp.firebase.entity.QFireBaseCloudMessage;
import com.talearnt.user.infomation.entity.QUser;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Log4j2
public class FcmTokenQueryRepository {
    
    private final JPAQueryFactory factory;
    private final QFireBaseCloudMessage fcm = QFireBaseCloudMessage.fireBaseCloudMessage;
    private final QUser user = QUser.user;
    public List<FireBaseCloudMessage> findActiveTokensByUserNo(Long userNo) {
        
        return factory.selectFrom(fcm)
                .join(fcm.user, user)
                .where(user.userNo.eq(userNo))
                .orderBy(fcm.updatedAt.desc())
                .fetch();
       
    }
    
    public long countTokensByUserNo(Long userNo) {
        return factory.select(fcm.count())
                .from(fcm)
                .join(fcm.user, user)
                .where(user.userNo.eq(userNo))
                .fetchOne();
    }
    
    public boolean existsByFcmToken(String fcmToken) {
        return factory.select(fcm.count())
                .from(fcm)
                .where(fcm.fcmToken.eq(fcmToken))
                .fetchOne() > 0;
    }
    
    public Optional<FireBaseCloudMessage> findByUserNoAndDeviceIdentifier(Long userNo, String deviceIdentifier) {
        return Optional.ofNullable(factory.selectFrom(fcm)
                .join(fcm.user, user)
                .where(user.userNo.eq(userNo)
                        .and(fcm.deviceIdentifier.eq(deviceIdentifier)))
                .fetchOne());
    }
    
    public List<FireBaseCloudMessage> findOldTokens() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        
        return factory.selectFrom(fcm)
                .where(fcm.updatedAt.before(cutoffDate))
                .fetch();
    }
    
    /**
     * 특정 사용자의 활성 FCM 토큰을 최신순으로 조회 (QueryDSL 방식)
     */
    public List<FireBaseCloudMessage> findActiveTokensByUserNoOrderByUpdatedAt(Long userNo, int limit) {
        return factory.selectFrom(fcm)
                .join(fcm.user, user)
                .where(user.userNo.eq(userNo))
                .orderBy(fcm.updatedAt.desc())
                .limit(limit)
                .fetch();  
    }
    
    /**
     * 특정 디바이스의 FCM 토큰 존재 여부 확인 (QueryDSL 방식)
     */
    public boolean existsByDeviceIdentifier(String deviceIdentifier) {
        return factory.select(fcm.count())
                .from(fcm)
                .where(fcm.deviceIdentifier.eq(deviceIdentifier))
                .fetchOne() > 0;
    }
}
