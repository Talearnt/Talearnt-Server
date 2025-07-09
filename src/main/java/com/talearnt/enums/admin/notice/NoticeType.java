package com.talearnt.enums.admin.notice;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;


@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum NoticeType {
    NOTICE("공지"),
    EVENT("이벤트 당첨"),
    UPDATE("업데이트");
    private final String type;

    NoticeType(String type) {
        this.type = type;
    }

    @JsonValue
    public String getType() {
        return type;
    }

    public static NoticeType fromString(String type) {
        for (NoticeType noticeType : NoticeType.values()) {
            if (noticeType.getType().equalsIgnoreCase(type) || noticeType.name().equalsIgnoreCase(type)) {
                return noticeType;
            }
        }
        return null;
    }
}
