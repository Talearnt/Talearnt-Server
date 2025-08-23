package com.talearnt.configure;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.talearnt.enums.common.ErrorCode;
import com.talearnt.util.exception.CustomRuntimeException;


/**
 * Firebase Admin SDK 설정을 위한 Configuration 클래스
 */
@Configuration
@Log4j2
public class FirebaseConfig {



    /**
     * Firebase Admin SDK 초기화
     */
    @PostConstruct
    public void initializeFirebase() {
        log.info("Firebase Admin SDK 초기화 준비 중...");
        try {
            // 이미 초기화된 Firebase 앱이 있는지 확인
            if (FirebaseApp.getApps().isEmpty()) {
                InputStream serviceAccount = new ClassPathResource("./src/main/resources/firebaseServiceAccountKey.json").getInputStream();
                
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                log.info("Firebase Admin SDK 초기화 완료");
            } else {
                log.info("Firebase Admin SDK가 이미 초기화되어 있습니다.");
            }
        } catch (IOException e) {
            log.error("Firebase Admin SDK 초기화 실패: {}", ErrorCode.FIREBASE_CANNOT_SETTING);
            throw new CustomRuntimeException(ErrorCode.FIREBASE_CANNOT_SETTING);
        }
        
    }
}
