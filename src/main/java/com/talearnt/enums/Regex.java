package com.talearnt.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Regex {
    NOT_USE_REGEX(""),
    EMAIL("^[A-Za-z0-9]{3,}@[A-Za-z0-9]{2,}\\.[A-Za-z]{2,}$"),
    PHONE_NUMBER("^[0-9]{10,11}$"),
    PASSWROD("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*()-_+=<>?]).{8,100}$"),
    GENDER("^(남자|여자)$"),
    EXCHANGE_TPYE("^(온라인|오프라인|온/오프라인)$"),
    EXCHANGE_DURATION("^(기간 미정|1개월|2개월|3개월|3개월 이상)$"),
    VERSION("^[0-9]+\\.[0-9]+$");
    private final String pattern;
}
