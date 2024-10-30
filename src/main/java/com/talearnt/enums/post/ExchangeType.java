package com.talearnt.enums;

public enum ExchangeType {
    온라인,
    오프라인,
    온_오프라인;

    @Override
    public String toString() {
        switch (this){
            case 온_오프라인 :
                return "온/오프라인";
            default:
                return name();
        }
    }
}
