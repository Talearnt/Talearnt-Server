package com.talearnt.enums.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Regex {
    NOT_USE_REGEX(""),
    EMAIL("^[A-Za-z0-9]{3,}@[A-Za-z0-9]{2,}\\.[A-Za-z]{2,}$"),
    PHONE_NUMBER("^[0-9]{11}$"),
    PASSWORD("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*()-_+=<>?]).{8,100}$"),
    GENDER("^(남자|여자)$"),
    EXCHANGE_TPYE("^(온라인|오프라인|온/오프라인)$"),
    EXCHANGE_DURATION("^(기간 미정|1개월|2개월|3개월|3개월 이상)$"),
    VERSION("^[0-9]+\\.[0-9]+$"),
    AUTH_CODE("^[0-9]{4}$"),
    CATEGORY_CODE("^[0-9]{4,}$"),
    CATEGORY_NAME("^[a-zA-Z0-9/ 가-힣]{2,}$"),
    NICKNAME("^[a-zA-Z0-9가-힣#]{2,12}$"),
    NAME("^[가-힣]{2,5}$"),
    //인증 문자 전송 타입 : 회원 가입 전용, 아이디 찾기 전용
    SMS_TYPE("^[a-zA-Z]{4,}$"),
    FILE_TYPE("^(image/jpeg|image/png|image/tiff|application/pdf)$"),
    FILE_EXTENSION("(?i)^.*\\.(jpg|jpeg|png|tiff|jfif|pdf)$");
    private final String pattern;
}
