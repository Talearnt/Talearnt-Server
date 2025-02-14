package com.talearnt.enums.post;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.talearnt.enums.common.ErrorCode;
import com.talearnt.util.exception.CustomRuntimeException;

public enum PostType {
    EXCHANGE,
    FREE,
    QUESTION,
    STUDY;

    @JsonCreator
    public static PostType from(String value) {
        for (PostType type : PostType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new CustomRuntimeException(ErrorCode.BAD_PARAMETER);
    }

}
