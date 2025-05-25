package com.talearnt.enums.common;

import com.talearnt.util.exception.CustomRuntimeException;
import lombok.extern.log4j.Log4j2;

@Log4j2
public enum ClientPathType {
    WEB,
    MOBILE;

    public static ClientPathType from(String value) {
        try{
            return ClientPathType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Client Path가 잘못되었습니다. path : {}", value);
            throw new CustomRuntimeException(ErrorCode.BAD_ACCESS_PATH);
        }
    }
}
