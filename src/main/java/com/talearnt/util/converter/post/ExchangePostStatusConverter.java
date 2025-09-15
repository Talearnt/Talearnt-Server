package com.talearnt.util.converter.post;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.enums.post.ExchangePostStatus;
import com.talearnt.util.exception.CustomRuntimeException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ExchangePostStatusConverter implements AttributeConverter<ExchangePostStatus, String> {
    @Override
    public String convertToDatabaseColumn(ExchangePostStatus exchangePostStatus) {
        if (exchangePostStatus == null)
            return null;

        switch (exchangePostStatus) {
            case NOW_RECRUITING:
                return "모집중";
            case RECRUITMENT_CLOSED:
                return "모집 마감";
            default:
                throw new CustomRuntimeException(ErrorCode.ILLEGAL_ARGUMENT_EXCEPTION);
        }
    }

    @Override
    public ExchangePostStatus convertToEntityAttribute(String s) {
        switch (s){
            case "모집중":
                return ExchangePostStatus.NOW_RECRUITING;
            case "모집 마감":
                return ExchangePostStatus.RECRUITMENT_CLOSED;
            default:
                throw new CustomRuntimeException(ErrorCode.ILLEGAL_ARGUMENT_EXCEPTION);
        }
    }
}
