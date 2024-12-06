package com.talearnt;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.util.exception.CustomException;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.response.CommonResponse;
import jakarta.mail.MessagingException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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

    //DB 반환 값이 1개여야 하는데 2개 이상 가져올 경우 발생
    @ExceptionHandler(IncorrectResultSizeDataAccessException.class)
    public ResponseEntity<CommonResponse<Object>> handleIncorrectResultSizeDataAccessException(IncorrectResultSizeDataAccessException e) {
        return CommonResponse.error(ErrorCode.DB_INCORRECT_RESULT_SIZE);
    }
    //메일 전송 실패 Exception
    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<CommonResponse<Object>> handleMessagingException(MessagingException e) {
        return CommonResponse.error(ErrorCode.MAIL_FAILED_RESPONSE);
    }

    //접근 권한이 없는 상태로 다른 페이지에 접근하면 발생하는 Exception
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CommonResponse<Object>> handleValidationExceptions(AccessDeniedException e) {
       return CommonResponse.error(ErrorCode.ACCESS_DENIED);
    }
    // Validated를 사용하는 메서드에서 발생한 유효성 검사 Exception
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CommonResponse<Object>> handleConstraintViolationExceptions(ConstraintViolationException e) {
        ConstraintViolation<?> violation = e.getConstraintViolations().stream().findFirst().orElse(null);

        if (violation != null) {
            String errorMessage = violation.getMessage();
            ErrorCode errorCode = ErrorCode.getErrorCode(errorMessage);
            return CommonResponse.error(errorCode);
        }
        return CommonResponse.error(ErrorCode.UNKNOWN_ERROR);
    }

    //Valid에서 Exception이 발생했을 경우 이 메소드를 사용합니다.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);

        if (fieldError != null) {
            ErrorCode errorCode = ErrorCode.getErrorCode(fieldError.getDefaultMessage());
            return CommonResponse.error(errorCode);
        }

        return CommonResponse.error(ErrorCode.UNKNOWN_ERROR);
    }

    // 지원하지 않은 URL로 요청했을 때 발생하는 Exception
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<CommonResponse<ErrorCode>> handleHttpRequestMethodNotSupportedException(NoResourceFoundException e) {
        return CommonResponse.error(ErrorCode.RESOURCE_NOT_FOUND);
    }

    // 지원하지 않는 메소드 타입으로 보냈을 경우 생기는 Exception
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<CommonResponse<ErrorCode>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return CommonResponse.error(ErrorCode.METHOD_NOT_SUPPORTED);
    }

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
