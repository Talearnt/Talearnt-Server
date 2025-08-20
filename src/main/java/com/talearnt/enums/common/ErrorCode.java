package com.talearnt.enums.common;

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
    ACCESS_DENIED("403-AUTH-04", "접근 권한이 없습니다."),
    INVALID_AUTH_CODE("400-AUTH-05", "인증 번호가 틀립니다. 확인 후 다시 입력해주세요"),
    UNVERIFIED_AUTH_CODE("400-AUTH-06", "인증되지 않은 인증 번호입니다. 인증 절차를 완료해 주세요."),
    AUTH_CODE_FORMAT_MISMATCH("400-AUTH-07", "인증 번호 코드를 제대로 입력해주세요."),
    AUTH_NOT_FOUND_PHONE_CODE("404-AUTH-08", "10분 이내로 해당 휴대 번호로 발송된 인증 번호가 없습니다."),
    AUTH_TOO_MANY_REQUEST("429-AUTH-09", "5회 연속 인증에 실패하였습니다. 잠시 후 다시 시도해주세요."),
    AUTH_NOT_FOUND_EMAIL_USER("404-AUTH-10", "10분이 지나 회원의 비밀번호를 변경할 수 없습니다. 다시 시도해주세요."),
    AUTH_METHOD_CONFLICT("400-AUTH-11", "이미 다른 SNS 혹은 TALEARNT 계정으로 가입된 아이디입니다. 다른 방법으로 로그인을 시도해주세요."),
    AUTH_SMS_TOO_MANY_REQUEST("429-AUTH-12","많은 요청이 왔습니다. 10분 후에 다시 시도해주세요."),
    EXPIRED_REFRESH_TOKEN("401-AUTH-13", "리프레시 토큰의 기간이 만료되었습니다."),

    //알림 관련 오류
    NOTIFICATION_NOT_FOUND("404-NOTIFICATION-01", "해당 알림을 찾을 수 없습니다."),
    NOTIFICATION_ACCESS_DENIED("403-NOTIFICATION-02", "해당 알림에 대한 접근 권한이 없습니다."),

    //메세지 관련 오류,
    MESSAGE_NOT_RESPONSE("500-MESSAGE-01", "메세지 전송에 실패했습니다. 잠시 후 다시 시도해 주세요."),

    //메일 관룐 오류
    MAIL_FAILED_RESPONSE("500-MAIL-01", "메일 전송이 실패하였습니다. 잠시 후 다시 시도해 주세요."),

    // 이용약관 관련 오류
    TERMS_TITLE_MISSING("400-TERMS-01", "이용 약관의 제목을 입력해주세요."),
    TERMS_INVALID_VERSION("400-TERMS-02", "이용약관 버전이 유효하지 않습니다."),
    TERMS_CONTENT_MISSING("400-TERMS-03", "이용 약관 내용을 15자 이상 입력해주세요."),

    // 사용자 관련 오류
    USER_NOT_FOUND("404-USER-01", "해당 회원을 찾을 수 없습니다."),
    DUPLICATE_USER_ID("400-USER-02", "해당 아이디는 이미 존재합니다."),
    INVALID_USER_INPUT("400-USER-03", "입력된 사용자 정보가 유효하지 않습니다."),
    USER_SUSPENDED("403-USER-04", "이 계정은 정지 상태입니다."),
    DUPLICATE_USER_NICKNAME("400-USER-05", "이미 등록된 닉네임입니다."),
    USER_ID_NOT_EMAIL_FORMAT("400-USER-06", "올바른 이메일 형식으로 입력해 주세요!"),
    USER_PHONE_NUMBER_FORMAT_MISMATCH("400-USER-07", "휴대폰 번호를 정확히 입력해 주세요!"),
    USER_PASSWORD_PATTERN_MISMATCH("400-USER-08", "영문,숫자,특수 문자를 반드시 포함한 비밀번호를 입력해 주세요!"),
    USER_PASSWORD_MISSING("400-USER-09", "비밀번호는 8자 이상으로 입력해 주세요"),
    USER_GENDER_MISSMATCH("400-USER-10", "성별은 남자,여자만 가능합니다."),
    USER_REQUIRED_NOT_AGREE("400-USER-11", "필수 약관에 대한 동의가 필요합니다."),
    USER_NOTHING_AGREE("400-USER-12", "약관 동의를 하지 않았습니다."),
    USER_NOT_FOUND_AGREE("404-USER-13", "약관 동의에 실패하였습니다. 반복적으로 발생할 경우 관리자에게 문의하세요."),
    USER_NOT_FOUND_PHONE_NUMBER("404-USER-14", "해당 휴대폰 번호로 가입한 회원이 없습니다."),
    USER_WITH_DRAWN("403-USER-15", "해당 아이디는 탈퇴한 회원입니다."),
    USER_PASSWROD_FAILED_DOUBLE_CHECK("400-USER-16", "두 개의 비밀번호가 일치하지 않습니다."),
    USER_PHONE_NUMBER_DUPLICATION("409-USER-17","이미 가입된 휴대폰 번호입니다."),
    USER_NAME_MISMATCH("400-USER-18","이름은 최소 2자, 최대 5자까지 입력 가능합니다."),
    USER_NICKNAME_MISMATCH("400-USER-19","한글,영문,숫자는 자유롭게 입력 가능하며, 특수문자는 #만 가능해요"),

    // 데이터베이스 관련 오류
    DB_CONNECTION_ERROR("500-DB-01", "데이터 베이스에 연결 실패했습니다."),
    DB_QUERY_ERROR("500-DB-02", "데이터 베이스에 잘못된 쿼리 입력되었습니다."),
    DB_TIMEOUT_ERROR("504-DB-03", "데이터베이스 응답 시간이 초과되었습니다."),
    DATA_INTEGRITY_VIOLATION("500-DB-04", "데이터 무결성 위반 오류가 발생했습니다."),
    DB_INCORRECT_RESULT_SIZE("500-DB-05", "결과 데이터 크기가 예상과 다릅니다. 1개만 반환해야하는데 2개 이상이 반환되었습니다."),

    // 요청 관련 오류
    BAD_REQUEST("400-REQ-01", "잘못된 요청입니다."),
    FILE_UPLOAD_EXTENSION_MISMATCH("415-REQ-02", "업로드 불가능한 확장자입니다. 확장자를 확인해주세요."),
    ILLEGAL_STATE_EXCEPTION("500-REQ-03", "현재 객체의 상태에서 해당 작업을 수행할 수 없습니다."),
    FILE_UPLOAD_TYPE_NOT_MATCH("400-REQ-04","업로드 파일 타입을 확인하세요."),
    FILE_UPLOAD_LENGTH_MISSING("400-REQ-05","업로드는 한 번에 최소 1개, 최대 5개까지 가능합니다."),
    FILE_UPLOAD_SIZE_OVER("413-REQ-06","5MB 이하의 파일만 업로드 가능합니다."),
    FILE_FAILED_UPLOAD("400-REQ-07","이미지 업로드에 실패하였습니다."),
    FILE_ACCESS_DENIED("403-REQ-08","해당 파일에 대한 접근 권한이 없습니다."),

    // 시스템 및 알 수 없는 오류
    SERVICE_UNAVAILABLE("503-SYSTEM-01", "서비스를 사용할 수 없습니다. 잠시 후 다시 시도해주세요."),
    INTERNAL_SERVER_ERROR("500-SYSTEM-02", "서버 내부 오류가 발생했습니다."),
    UNKNOWN_ERROR("500-UNKNOWN-01", "알 수 없는 오류가 발생했습니다. 관리자에게 문의하세요."),

    // 키워드 관련 오류
    KEYWORD_CODE_DUPLICATION("400-KEYWORD-01","대분류 키워드 코드가 중복되었습니다. 다른 번호를 입력하세요."),
    KEYWORD_NAME_DUPLICATION("400-KEYWORD-02","대분류 키워드 이름이 중복되었습니다. 다른 이름을 입력하세요."),
    KEYWORD_CODE_MISMATCH("400-KEYWORD-03","키워드 코드가 잘못 입력되었습니다. 코드를 확인하세요! (최소 단위 1,000)"),
    KEYWORD_NAME_MISMATCH("400-KEYWORD-04","키워드 이름이 잘못 입력되었습니다. 이름를 확인하세요! (최소 2자 이상)"),
    KEYWORD_CATEGORY_CODE_MISMATCH("400-KEYWORD-05","대분류 키워드 코드가 없습니다."),
    KEYWORD_TALENT_CODE_DUPLICATION("400-KEYWORD-06","재능 분류 키워드 코드가 중복됩니다."),
    KEYWORD_TALENT_NAME_DUPLICATION("400-KEYWORD-07","재능 분류 키워드 이름이 중복되었습니다. 다른 이름을 입력하세요."),
    KEYWORD_CATEGORY_NOT_FOUND("404-KEYWORD-08","존재하지 않은 키워드입니다."),

    //댓글+답글 관련 오류
    COMMENT_MISMATCH_POST_NUMBER("400-COMMENT-01","댓글을 달 수 없습니다. 올바른 게시글 번호를 입력하세요."),
    COMMENT_CONTENT_OVER_LENGTH("400-COMMENT-02","댓글은 3자 이상, 300자 이하 입력 가능합니다."),
    COMMENT_NOT_FOUND_POST("404-COMMENT-03","해당 게시글이 없거나, 삭제되어 댓글을 달 수 없습니다."),
    COMMENT_FAILED_CALL_LIST("400-COMMENT-04","댓글 목록 조회에 실패했습니다. 필요 값을 확인하세요. (Page-Web) OR (LastNo-Mobile)"),
    COMMENT_MISMATCH_NUMBER("400-COMMENT-05","댓글을 달 수 없습니다. 올바른 댓글 번호를 입력하세요."),
    COMMENT_ACCESS_DINED("403-COMMENT-06","삭제된 댓글이거나, 본인 댓글이 아닙니다."),
    COMMENT_FAILED_UPDATE("400-COMMENT-07","댓글 수정에 실패했습니다 - (0개 또는 복수 개 업데이트 될 가능성 있음)"),
    COMMENT_FAILED_DELETE("400-COMMENT-08","댓글 삭제에 실패했습니다 - (0개 또는 복수 개 삭제 될 가능성 있음)"),
    COMMENT_MISMATCH_REPLY_NUMBER("404-COMMENT-09","해당 댓글 번호가 없거나 삭제되어 댓글을 달 수 없습니다."),
    REPLY_NOT_FOUND("404-COMMENT-10","해당 답글 번호가 없거나 이미 삭제된 답글 번호입니다."),
    REPLY_FAILED_CALL_LIST("400-COMMENT-11","답글 목록 조회에 실패했습니다. 필요 값을 확인하세요. (Page-Web) OR (LastNo-Mobile)"),
    COMMENT_NOT_FOUND("404-COMMENT-12","해당 댓글을 찾을 수 없습니다."),

    //게시글 관련 오류
    POST_TITLE_LENGTH_MISSING("400-POST-01", "제목을 2글자 이상 입력해 주세요."),
    POST_CONTENT_MIN_LENGTH("400-POST-02", "내용 20글자 이상 입력해 주세요."),
    POST_REQUEST_MISSING("400-POST-03", "필수 정보를 입력해 주세요."),
    POST_BAD_REQUEST("400-POST-04", "잘못된 값이 넘어왔습니다. 입력 값을 확인하세요."),
    POST_KEYWORD_LENGTH_MISSING("400-POST-05", "재능 키워드를 선택해 주세요."),
    POST_KEYWORD_LENGTH_OVER("400-POST-06", "재능 키워드는 5개 이하 선택 가능합니다."),
    POST_NOT_FOUND("404-POST-07", "해당 게시글을 찾을 수 없습니다."),
    POST_ACCESS_DENIED("403-POST-08", "해당 게시글에 대한 권한이 없습니다."),
    POST_GIVE_MY_TALENT_NOT_FOUND("404-POST-09", "나의 재능 - 주고 싶은 재능에 존재하지 않은 키워드입니다."),
    POST_DURATION_MISSING("400-POST-10", "진행 기간을 선택해 주세요."),
    POST_FAILED_UPDATE("400-POST-11","게시글 수정에 실패하였습니다."),
    POST_FAILED_DELETE("400-POST-12","게시글 삭제에 실패하였습니다."),
    POST_FAILED_CALL_LIST("400-POST-13","게시글 목록 조회에 실패했습니다. 필요 값을 확인하세요. (Page-Web) OR (LastNo-Mobile)"),

    //나의 재능 관련 오류
    MY_TALENT_KEYWORD_NOT_REGISTERED("404-MYTALENT-01","나의 재능을 등록하지 않았습니다."),

    // 페이지 관련 오류
    PAGE_MIN_NUMBER("400-PAGE-01", "페이지 번호는 0보다 작을 수 없습니다."),
    PAGE_OVER_MAX_NUMBER("400-PAGE-02", "해당 페이지 번호는 유효하지 않는 번호입니다."),

    //파이어베이스 관련 오류
    FIREBASE_CANNOT_SETTING("500-FIREBASE-01", "파이어베이스 SDK 설정에 실패했습니다."),

    //서버 오류
    ILLEGAL_ARGUMENT_EXCEPTION("400-SERVER-01", "잘못된 값을 입력하셨습니다. 입력 값을 확인하세요."),

    //이미 만들어져 공통으로 사용되는 오류
    METHOD_NOT_SUPPORTED("405-COMMON-01", "지원하지 않는 HTTP 메서드입니다."),
    RESOURCE_NOT_FOUND("404-COMMON-02", "요청한 리소스를 찾을 수 없습니다. 경로를 확인하세요."),
    BAD_PARAMETER("400-COMMON-03", "잘못된 값이 넘어왔습니다. 입력하신 내용을 확인해주세요."),
    BAD_NULL_PARAMETER("400-COMMON-04", "입력한 값이 NULL입니다. 입력하신 내용을 확인해주세요."),
    BAD_ACCESS_PATH("400-COMMON-05","Path값이 일치하지 않습니다.");
    private final String code;
    private final String message;

    // OpenAPI용 ApiResponse 생성 메서드
    public ApiResponse getApiResponse() {
        return new ApiResponse()
                .description(this.getMessage())
                .content(new Content().addMediaType("application/json;charset=UTF-8",
                        new MediaType().schema(new Schema()
                                .example("{ " +
                                        "\n\t \"data\": null," +
                                        "\n\t \"errorCode\": \"" + this.getCode() + "\"," +
                                        "\n\t \"errorMessage\": \"" + this.getMessage() + "\"," +
                                        "\n\t \"success\": false" +
                                        "\n}"))));
    }

    /**
     * @param code : Error 코드
     *             ex) 400-USER-03
     *             Dynamic Valid ErrorCode 반환.
     */
    public static ErrorCode getErrorCode(String code) {
        for (ErrorCode e : values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Invalid error code: " + code);
    }


}
