package com.talearnt.util.exception;

import com.talearnt.enums.ErrorCode;

public class CustomRuntimeException extends Exception{
    private final ErrorCode errorCode;

    public CustomRuntimeException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CustomRuntimeException(ErrorCode errorCode, Throwable cause){
        super(errorCode.getMessage(),cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode(){
        return errorCode;
    }
}
