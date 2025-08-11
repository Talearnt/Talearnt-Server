package com.talearnt.util.converter.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class ListStringConverter {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String DELIMITER = ",";

    /**
     * 정수 리스트를 콤마로 구분된 문자열로 변환합니다.
     */
    public static String integerListToString(List<Integer> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return list.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(DELIMITER));
    }

    /**
     * 콤마로 구분된 문자열을 정수 리스트로 변환합니다.
     */
    public static List<Integer> stringToIntegerList(String str) {
        if (str == null || str.isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.stream(str.split(DELIMITER))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    /**
     * 객체 리스트를 JSON 문자열로 변환합니다.
     */
    public static <T> String listToJsonString(List<T> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }

        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            log.error("리스트를 JSON 문자열로 변환 중 오류 발생: {}", e.getMessage());
            return "[]";
        }
    }

    /**
     * JSON 문자열을 객체 리스트로 변환합니다.
     */
    public static <T> List<T> jsonStringToList(String jsonStr, Class<T> elementType) {
        if (jsonStr == null || jsonStr.isEmpty() || "[]".equals(jsonStr)) {
            return Collections.emptyList();
        }

        try {
            return objectMapper.readValue(jsonStr,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, elementType));
        } catch (JsonProcessingException e) {
            log.error("JSON 문자열을 리스트로 변환 중 오류 발생: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * JSON 문자열을 복잡한 타입의 리스트로 변환합니다.
     */
    public static <T> List<T> jsonStringToComplexList(String jsonStr, TypeReference<List<T>> typeReference) {
        if (jsonStr == null || jsonStr.isEmpty() || "[]".equals(jsonStr)) {
            return Collections.emptyList();
        }

        try {
            return objectMapper.readValue(jsonStr, typeReference);
        } catch (JsonProcessingException e) {
            log.error("JSON 문자열을 복잡한 타입의 리스트로 변환 중 오류 발생: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}