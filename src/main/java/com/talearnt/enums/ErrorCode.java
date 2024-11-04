package com.talearnt.enums;

import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    // 인증 관련 오류
    INVALID_TOKEN("401-AUTH-01", "올바르지 않은 인증 토큰입니다."),
    EXPIRED_TOKEN("401-AUTH-02", "로그인 후 다시 이용해주세요."),
    INVALID_CREDENTIALS("401-AUTH-03", "아이디 또는 비밀번호를 확인해주세요!"),
    ACCESS_DENIED("403-AUTH-04", "접근이 거부되었습니다."),
    INVALID_AUTH_CODE("400-AUTH-05","인증 번호가 틀립니다. 확인 후 다시 입력해주세요"),

    // 사용자 관련 오류
    USER_NOT_FOUND("404-USER-01", "해당 회원을 찾을 수 없습니다."),
    DUPLICATE_USER_ID("400-USER-02", "해당 아이디는 이미 존재합니다."),
    INVALID_USER_INPUT("400-USER-03", "입력된 사용자 정보가 유효하지 않습니다."),
    USER_SUSPENDED("403-USER-04", "이 계정은 정지 상태입니다."),
    DUPLICATE_USER_NICKNAME("400-USER-05", "해당 닉네임은 이미 존재합니다."),
    USER_ID_NOT_EMAIL_FORMAT("400-USER-06","올바른 이메일 형식으로 입력해 주세요!"),
    USER_PHONE_NUMBER_FORMAT_MISMATCH("400-USER-07","휴대폰 번호를 정확히 입력해 주세요!"),

    // 데이터베이스 관련 오류
    DB_CONNECTION_ERROR("500-DB-01", "데이터 베이스에 연결 실패했습니다."),
    DB_QUERY_ERROR("500-DB-02", "데이터 베이스에 잘못된 쿼리 입력되었습니다."),
    DB_TIMEOUT_ERROR("504-DB-03", "데이터베이스 응답 시간이 초과되었습니다."),
    DATA_INTEGRITY_VIOLATION("500-DB-04", "데이터 무결성 위반 오류가 발생했습니다."),

    // 파일 및 네트워크 관련 오류
    FILE_NOT_FOUND("404-FILE-01", "요청된 파일을 찾을 수 없습니다."),
    NETWORK_ERROR("503-NETWORK-01", "네트워크 오류로 인해 요청을 처리할 수 없습니다."),

    // 요청 관련 오류
    BAD_REQUEST("400-REQ-01", "잘못된 요청입니다."),
    UNSUPPORTED_MEDIA_TYPE("415-REQ-02", "지원되지 않는 미디어 타입입니다."),
    ILLEGAL_STATE_EXCEPTION("500-REQ-03", "현재 객체의 상태에서 해당 작업을 수행할 수 없습니다."),

    // 시스템 및 알 수 없는 오류
    SERVICE_UNAVAILABLE("503-SYSTEM-01", "서비스를 사용할 수 없습니다. 잠시 후 다시 시도해주세요."),
    INTERNAL_SERVER_ERROR("500-SYSTEM-02", "서버 내부 오류가 발생했습니다."),
     UNKNOWN_ERROR("500-UNKNOWN-01", "알 수 없는 오류가 발생했습니다. 관 리자에게 문의하세요."),

    //게시글 관련 오류
    POST_TITLE_OVER_LENGTH("400-POST-01","제목 글자 수 제한을 확인해주세요."),
    POST_TITLE_MISSING("400-POST-02", "제목을 입력해주세요."),
    POST_CONTENT_MISSING("400-POST-03", "내용을 입력해주세요."),
    POST_CONTENT_MIN_LENGTH("400-POST-04", "내용 20글자 이상 필수 입력입니다!"),
    POST_REQUEST_MISSING("400-POST-05", "필수 항목을 입력해 주세요."),
    POST_BAD_REQUEST("400-POST-06", "잘못된 값이 넘어왔습니다. 입력 값을 확인하세요."),
    POST_OVER_REQUEST_LENGTH("400-POST-07", "최대 5개의 재능만 선택하실 수 있습니다"),
    POST_NOT_FOUND("404-POSET-08", "해당 게시글을 찾을 수 없습니다."),


    //서버 오류
    ILLEGAL_ARGUMENT_EXCEPTION("400-SERVER-01", "잘못된 값을 입력하셨습니다. 입력 값을 확인하세요."),

    //이미 만들어져 공통으로 사용되는 오류
    METHOD_NOT_SUPPORTED("405-COMMON-01", "지원하지 않는 HTTP 메서드입니다."),
    RESOURCE_NOT_FOUND("404-COMMON-02", "요청한 리소스를 찾을 수 없습니다. 경로를 확인하세요.");


    private final String code;
    private final String message;

    // OpenAPI용 ApiResponse 생성 메서드
    public ApiResponse getApiResponse() {
        return new ApiResponse()
                .description(this.getMessage())
                .content(new Content().addMediaType("application/json;charset=UTF-8",
                        new MediaType().schema(new Schema()
                                .example("{ "+
                                        "\n\t \"data\": null," +
                                        "\n\t \"errorCode\": \"" + this.getCode() + "\"," +
                                        "\n\t \"errorMessage\": \""+ this.getMessage() + "\"," +
                                        "\n\t \"success\": false" +
                                        "\n}"))));
    }

    /**
     * @param code : Error 코드
     *             ex) 400-USER-03
     * Dynamic Valid ErrorCode 반환.
     * */
    public static ErrorCode getErrorCode(String code) {
        for (ErrorCode e : values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Invalid error code: " + code);
    }


}
