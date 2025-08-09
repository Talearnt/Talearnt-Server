package com.talearnt.util.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SplitUtil {


    /**
     * 쉼표로 구분된 문자열을 Integer 리스트로 변환합니다.
     *
     * @param commaSeparatedString 쉼표로 구분된 숫자 문자열 (예: "1001,1002,1003")
     * @return 변환된 Integer 리스트
     */
    public static List<Integer> splitToIntegerList(String commaSeparatedString) {
        if (commaSeparatedString == null || commaSeparatedString.isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.stream(commaSeparatedString.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }


    public static List<String> splitStringToList(String value){
        if (value == null) return List.of();
        return List.of(value.split(","));
    }

    /** Search Keyword를 정제하는 메소드*/
    public static String createSearchKeywordForBooleanMode(String keyword){

        //값이 없거나, 비어 있으면 null 반환
        if (keyword == null || keyword.trim().isEmpty()){
            return null;
        }

        String trimKeyword = keyword.trim();

        //있을 경우 Boolean Mode 검색 결과에 맞게 변경
        return Arrays.stream(trimKeyword.split("\\s"))
                .map(word -> "+"+word)
                .collect(Collectors.joining(" "));
    }

}
