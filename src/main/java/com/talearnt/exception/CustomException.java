package com.talearnt.exception;

import com.talearnt.enums.ErrorCode;

public class CustomException extends Exception{
    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CustomException(ErrorCode errorCode,Throwable cause){
        super(errorCode.getMessage(),cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode(){
        return errorCode;
    }
}
