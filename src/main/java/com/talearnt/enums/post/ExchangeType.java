package com.talearnt.enums.post;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.talearnt.enums.common.ErrorCode;
import com.talearnt.util.exception.CustomRuntimeException;

public enum ExchangeType {
    온라인("온라인"),
    오프라인("오프라인"),
    온_오프라인("온/오프라인");

    private final String type;

    ExchangeType(String type){
        this.type=type;
    }

    @JsonValue
    public String getType(){
        return type;
    }

    @JsonCreator
    public static ExchangeType fromFE(String value){
        if (value == null || value.isEmpty()) return null;

        for (ExchangeType exchangeType : ExchangeType.values()){
            if(exchangeType.type.equals(value) || exchangeType.name().equals(value)){
                return  exchangeType;
            }
        }
        throw new CustomRuntimeException(ErrorCode.ILLEGAL_ARGUMENT_EXCEPTION);
    }

}
