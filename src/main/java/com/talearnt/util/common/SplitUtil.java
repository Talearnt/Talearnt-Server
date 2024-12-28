package com.talearnt.util.common;

import java.util.List;

public class SplitUtil {

    public static List<String> splitStringToList(String value){
        if (value == null) return List.of();
        return List.of(value.split(","));
    }

}
