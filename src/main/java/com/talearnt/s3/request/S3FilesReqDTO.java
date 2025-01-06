package com.talearnt.s3.request;

import lombok.*;

import java.util.List;

@Builder
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class S3FilesReqDTO {
    private List<String> fileNames;
    private String fileType; // 이미지, PDF등 구분
}
