package com.talearnt.util.common;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SplitUtil {

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
