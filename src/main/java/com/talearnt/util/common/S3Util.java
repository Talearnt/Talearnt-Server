package com.talearnt.util.common;

import com.talearnt.s3.entity.FileUpload;

import java.util.List;
import java.util.stream.Stream;

public class S3Util {
    public static final String S3_DOMAIN_BASE_URL = "https://talearnt-sever-images-upload-bucket.s3.ap-northeast-2.amazonaws.com";

    public static List<String> splitStringToList(String value){
        if (value == null) return List.of();
        return Stream.of(value.split(",")).map(str -> S3_DOMAIN_BASE_URL+str).toList();
    }

    /** 삭제할 이미지 파일 찾기
     * DB 데이터에서 요청 받은 파일이랑 비교했을 때 존재하지 않으면 삭제할 파일*/
    public static List<FileUpload> willDeleteFileUploads(List<FileUpload> fileUploads, List<String> requestUrls){
        return fileUploads.stream()
                .filter(fileUpload -> !requestUrls.contains(S3_DOMAIN_BASE_URL+fileUpload.getUrl()))
                .toList();
    }

    /** 추가할 이미지 파일 찾기
     * 요청 받은 이미지에서 DB에 존재하지 않은 데이터는 추가할 파일*/
    public static List<String> willAddFileUploadUrls(List<FileUpload> fileUploads, List<String>  requestUrls){
        return requestUrls.stream()
                .filter(requestUrl -> fileUploads.stream().noneMatch(fileUpload -> fileUpload.getUrl().equals(requestUrl)))
                .toList();
    }

}
