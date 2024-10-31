package com.talearnt.enums.post;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.talearnt.enums.ErrorCode;
import com.talearnt.util.exception.CustomRuntimeException;

public enum ExchangePostStatus {
    모집중("모집중"),
    모집_완료("모집 완료");

    private final String status;

    ExchangePostStatus(String status) {
        this.status = status;
    }

    @JsonValue
    public String getStatus() {
        return status;
    }

    @JsonCreator
    public static ExchangePostStatus fromFE(String value) {
        for (ExchangePostStatus exchangePostStatus : ExchangePostStatus.values()) {
            if (exchangePostStatus.status.equals(value)) {
                return exchangePostStatus;
            }
        }
        throw new CustomRuntimeException(ErrorCode.ILLEGAL_ARGUMENT_EXCEPTION);
    }
}
