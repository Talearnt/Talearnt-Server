package com.talearnt.util.common;

import java.util.List;
import java.util.stream.Stream;

public class S3Util {
    public static final String S3_DOMAIN_BASE_URL = "https://talearnt-sever-images-upload-bucket.s3.ap-northeast-2.amazonaws.com";

    public static List<String> splitStringToList(String value){
        if (value == null) return List.of();
        return Stream.of(value.split(",")).map(str -> S3_DOMAIN_BASE_URL+str).toList();
    }

}
