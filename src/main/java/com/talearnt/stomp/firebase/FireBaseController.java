package com.talearnt.stomp.firebase;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.stomp.firebase.request.FcmTokenReqDTO;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.version.RestControllerV1;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerV1
@Log4j2
@RequiredArgsConstructor
@Tag(name = "FireBase Cloud Message", description = "FCM 토큰 관리 및 메시지 전송")
public class FireBaseController implements FireBaseApi {

    private final FcmService fcmService;

    /**
     * FCM 토큰 저장
     * 로그인 성공 후 FE에서 FCM 토큰을 넘겨주는 HTTP POST 메소드
     * 조건)
     * - 로그인이 되어 있어야 한다. (Binder에서 해결)
     * - FCM 토큰이 유효해야 한다.
     * - 디바이스별로 하나의 토큰만 유지한다.
     */
    @PostMapping("/fcm/token")
    public ResponseEntity<CommonResponse<String>> saveFcmToken(@RequestBody @Valid FcmTokenReqDTO requestDTO) {
        log.info("FCM 토큰 저장 요청: userNo={}, deviceIdentifier={}", 
                requestDTO.getUserInfo().getUserNo(), requestDTO.getDeviceIdentifier());
        
        try {
            Long fcmTokenNo = fcmService.saveFcmToken(
                    requestDTO.getUserInfo().getUserNo(),
                    requestDTO.getFcmToken(),
                    requestDTO.getDeviceIdentifier(),
                    requestDTO.getDeviceInfo()
            );
            
            log.info("FCM 토큰 저장 완료: userNo={}, fcmTokenNo={}", 
                    requestDTO.getUserInfo().getUserNo(), fcmTokenNo);
            
            return CommonResponse.success("FCM 토큰이 성공적으로 저장되었습니다.");
            
        } catch (Exception e) {
            log.error("FCM 토큰 저장 실패: {}", ErrorCode.FIREBASE_CANNOT_SAVE_TOKEN);
            throw new CustomRuntimeException(ErrorCode.FIREBASE_CANNOT_SAVE_TOKEN);
        }
    }

}
