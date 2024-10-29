package com.talearnt.enums;

public enum ExchangePostStatus {
    모집중,
    모집_완료;

    @Override
    public String toString() {
        switch (this){
            case 모집_완료 :
                return "모집 완료";
            default:
                return name();
        }
    }
}
