package com.talearnt.configure;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ConditionalOnProperty(value = "firebase.enabled", havingValue = "true", matchIfMissing = true)
@Configuration
@Log4j2
public class FirebaseConfig {
    /**
     * Firebase Admin SDK 초기화
     */
    @Value("${firebase.service-account-file:firebaseServiceAccountKey.json}")
    private String serviceAccountFile;

    @PostConstruct
    public void initializeFirebase() {
        log.info("Firebase Admin SDK 초기화 준비 중... : {}", serviceAccountFile);
        try {
            File file = new File(serviceAccountFile);
            log.info("Firebase Admin SDK 파일 경로: {}", file.getAbsolutePath());
            if (!file.exists()) {
                log.warn("JSON 파일이 존재하지 않습니다. Firebase Admin SDK 초기화를 건너뜁니다.");
                return;
            }

            // 이미 초기화된 Firebase 앱이 있는지 확인
            if (FirebaseApp.getApps().isEmpty()) {
                try (FileInputStream serviceAccount = new FileInputStream(file)) {
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                            .build();

                    if (FirebaseApp.getApps().isEmpty()) {
                        FirebaseApp.initializeApp(options);
                        log.info("Firebase Admin SDK 초기화 완료");
                    }
                } catch (Exception e) {
                    log.error("Firebase Admin SDK 초기화 중 오류 발생: {}", e.getMessage());
                    throw new CustomRuntimeException(ErrorCode.FIREBASE_CANNOT_SETTING);
                }
            } else {
                log.info("Firebase Admin SDK가 이미 초기화되어 있습니다.");
            }
        } catch (Exception e) {
            log.error("Firebase Admin SDK 초기화 실패: {}", ErrorCode.FIREBASE_CANNOT_SETTING);
            throw new CustomRuntimeException(ErrorCode.FIREBASE_CANNOT_SETTING);
        }
        
    }
}
