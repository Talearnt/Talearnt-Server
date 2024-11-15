package com.talearnt.enums;

import com.talearnt.util.exception.CustomRuntimeException;
import lombok.extern.log4j.Log4j2;

@Log4j2
public enum Gender {
    남자,
    여자;
    public static Gender fromString(String value) {
        if (value == null) {
            log.error("성별 값이 Null로 넘어왔습니다.");
            throw new CustomRuntimeException(ErrorCode.BAD_NULL_PARAMETER);
        }

        switch (value.toLowerCase()) { // 대소문자 구분 없이 처리
            case "male":
                return 남자;
            case "female":
                return 여자;
            default:
                log.error("성별 값이 제대로 넘어오지 않았습니다.");
                throw new CustomRuntimeException(ErrorCode.BAD_PARAMETER);
        }
    }
}
