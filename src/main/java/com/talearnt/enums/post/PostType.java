package com.talearnt.enums.post;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.talearnt.enums.common.ErrorCode;
import com.talearnt.util.exception.CustomRuntimeException;

public enum PostType {
    EXCHANGE("재능 교환 게시판"),
    FREE("자유 게시판"),
    QUESTION("질문 게시판"),
    STUDY("스터디 게시판");

    private final String type;

    PostType(String type){this.type = type;}

    @JsonValue
    public String getType(){
        return type;
    }

    @JsonCreator
    public static PostType from(String value) {
        if (value == null || value.isEmpty()) return null;
        for (PostType type : PostType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new CustomRuntimeException(ErrorCode.BAD_PARAMETER);
    }

}
