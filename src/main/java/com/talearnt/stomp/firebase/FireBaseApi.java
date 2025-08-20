package com.talearnt.stomp.firebase;

import com.talearnt.stomp.firebase.request.FcmTokenReqDTO;
import com.talearnt.util.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface FireBaseApi {

    @Operation(summary = "FCM 토큰 저장",
            description = "<h2>내용</h2>" +
                    "<p>사용자의 FCM 토큰을 저장하거나 업데이트합니다.</p>" +
                    "<p>로그인 성공 후 클라이언트에서 FCM 토큰을 서버로 전송하여 푸시 알림을 받을 수 있도록 합니다.</p>" +
                    "<p>동일한 디바이스에서 토큰이 변경되면 자동으로 업데이트됩니다.</p>" +
                    "<hr>" +
                    "<h2>Request</h2>" +
                    "<ul>" +
                        "<li>fcmToken : FCM 토큰 (필수) - Firebase에서 발급받은 고유 토큰</li>" +
                        "<li>deviceIdentifier : 디바이스 식별자 (선택) - 디바이스를 구분하기 위한 고유 식별자</li>" +
                        "<li>deviceInfo : 디바이스 정보 (선택) - 디바이스 모델명, OS 버전 등</li>" +
                    "</ul>" +
                    "<hr>" +
                    "<h2>Response</h2>" +
                    "<p>성공 시 저장 완료 메시지를 반환합니다.</p>" +
                    "<hr>" +
                    "<h2>동작 방식</h2>" +
                    "<ol>" +
                        "<li>사용자 인증 확인 (JWT 토큰 검증)</li>" +
                        "<li>기존 디바이스 토큰 존재 여부 확인</li>" +
                        "<li>기존 토큰이 있으면 업데이트, 없으면 새로 생성</li>" +
                        "<li>디바이스별로 하나의 토큰만 유지</li>" +
                    "</ol>" +
                    "<hr>" +
                    "<h2>사용 시나리오</h2>" +
                    "<ul>" +
                        "<li>앱 최초 실행 시 FCM 토큰 발급 후 저장</li>" +
                        "<li>앱 재설치 시 새로운 FCM 토큰으로 업데이트</li>" +
                        "<li>디바이스 변경 시 새로운 디바이스 정보와 함께 토큰 저장</li>" +
                        "<li>토큰 갱신 시 자동 업데이트</li>" +
                    "</ul>" +
                    "<hr>" +
                    "<h2>주의사항</h2>" +
                    "<ul>" +
                        "<li>로그인이 되어 있어야 합니다 (JWT 토큰 필요)</li>" +
                        "<li>FCM 토큰은 유효해야 합니다</li>" +
                        "<li>디바이스별로 하나의 토큰만 유지됩니다</li>" +
                        "<li>토큰이 유효하지 않으면 자동으로 삭제됩니다</li>" +
                    "</ul>"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "FCM 토큰 저장 성공"),
            @ApiResponse(responseCode = "400", ref = "BAD_PARAMETER"),
            @ApiResponse(responseCode = "401", ref = "EXPIRED_TOKEN"),
            @ApiResponse(responseCode = "404", ref = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<CommonResponse<String>> saveFcmToken(@RequestBody @Valid FcmTokenReqDTO requestDTO);

}
