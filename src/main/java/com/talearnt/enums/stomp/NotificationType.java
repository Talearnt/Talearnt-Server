package com.talearnt.enums.stomp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.extern.log4j.Log4j2;

@Log4j2
public enum NotificationType {
    INTERESTING_KEYWORD("관심 키워드"),
    COMMENT("댓글");

    private final String type;

    NotificationType(String type) {
        this.type = type;
    }

    @JsonValue
    public String getType() {
        return type;
    }

    @JsonCreator
    public static NotificationType from(String value) {
        if (value == null || value.isEmpty()) return null;
        for (NotificationType notificationType : NotificationType.values()) {
            if (notificationType.name().equalsIgnoreCase(value) || notificationType.type.equalsIgnoreCase(value)) {
                return notificationType;
            }
        }
        log.error("해당 알림 타입이 없습니다 : {}", value);
        throw new IllegalArgumentException("해당 알림 타입이 없습니다. " + value);
    }
}
