package com.talearnt.stomp.firebase.repository;

import com.talearnt.stomp.firebase.entity.FireBaseCloudMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FcmTokenRepository extends JpaRepository<FireBaseCloudMessage, Long> {
    
    List<FireBaseCloudMessage> findByUser_UserNo(Long userNo);
    
    Optional<FireBaseCloudMessage> findByFcmToken(String fcmToken);
    
    Optional<FireBaseCloudMessage> findByUser_UserNoAndFcmToken(Long userNo, String fcmToken);
    
    Optional<FireBaseCloudMessage> findByUser_UserNoAndDeviceIdentifier(Long userNo, String deviceIdentifier);
    
    @Modifying
    @Query("DELETE FROM FireBaseCloudMessage f WHERE f.fcmToken = :fcmToken")
    void deleteByFcmToken(@Param("fcmToken") String fcmToken);
    
    @Modifying
    @Query("DELETE FROM FireBaseCloudMessage f WHERE f.user.userNo = :userNo")
    void deleteByUserNo(@Param("userNo") Long userNo);
    
    @Modifying
    @Query("DELETE FROM FireBaseCloudMessage f WHERE f.fcmToken IN :fcmTokens")
    void deleteByFcmTokens(@Param("fcmTokens") List<String> fcmTokens);
}
