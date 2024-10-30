package com.talearnt;

import com.talearnt.enums.ErrorCode;
import com.talearnt.util.exception.CustomException;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.response.CommonResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 연관된 Class = /Enums/ErrorCode,
 *              /Util/CommonResponse
 *
 * Exception 전역 관리하는 Class 입니다.
 * 이 곳에 Exception이 터질 요인들을 모아두고 관리합니다.
 * */
@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 현재 객체의 상태에서 해당 작업을 수행할 수 없습니다.
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<CommonResponse<ErrorCode>> handleIllegalStateException(IllegalStateException e) {
        return CommonResponse.error(ErrorCode.ILLEGAL_STATE_EXCEPTION);
    }

    // Custom Runtime Exception
    @ExceptionHandler(CustomRuntimeException.class)
    public ResponseEntity<CommonResponse<ErrorCode>> handleCustomRuntimeException(CustomRuntimeException e) {
        return CommonResponse.error(e.getErrorCode());
    }

    // Custom Exception
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CommonResponse<ErrorCode>> handleCustomException(CustomException e) {
        return CommonResponse.error(e.getErrorCode());
    }

    /** 최상위 Exception,
     *  다른 ExceptionHandler에 속하지 않은 오류들은 이곳으로 도착합니다.
     *  Log를 통하여 세부적인 Exception 이름을 알 수 있습니다.
     * */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<ErrorCode>> handleException(Exception e) {
        // 세부 Exception 이름 디버깅용
        e.printStackTrace();
        return CommonResponse.error(ErrorCode.UNKNOWN_ERROR);
    }

}
