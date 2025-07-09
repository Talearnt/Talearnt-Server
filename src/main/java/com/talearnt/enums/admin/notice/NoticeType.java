package com.talearnt.enums.admin.notice;

public enum NoticeType {
    NOTICE("공지"),
    EVENT("이벤트 당첨"),
    UPDATE("업데이트");
    private final String type;

    NoticeType(String type) {
        this.type = type;
    }

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
