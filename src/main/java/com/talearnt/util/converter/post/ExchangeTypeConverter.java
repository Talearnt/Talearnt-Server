package com.talearnt.util.converter.post;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.enums.post.ExchangeType;
import com.talearnt.util.exception.CustomRuntimeException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ExchangeTypeConverter implements AttributeConverter<ExchangeType, String> {

    @Override
    public String convertToDatabaseColumn(ExchangeType exchangeType) {
        if (exchangeType == null)
            return null;

        switch (exchangeType) {
            case 온_오프라인:
                return "온/오프라인";
            case 온라인:
                return "온라인";
            case 오프라인:
                return "오프라인";
            default:
               throw new CustomRuntimeException(ErrorCode.ILLEGAL_ARGUMENT_EXCEPTION);

        }
    }

    @Override
    public ExchangeType convertToEntityAttribute(String s) {
        switch (s) {
            case "온/오프라인":
                return ExchangeType.온_오프라인;
            case "온라인":
                return ExchangeType.온라인;
            case "오프라인":
                return ExchangeType.오프라인;
            default:
                throw new CustomRuntimeException(ErrorCode.ILLEGAL_ARGUMENT_EXCEPTION);
        }
    }
}