package com.talearnt.s3.request;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.enums.common.Regex;
import com.talearnt.util.valid.DynamicValid;
import com.talearnt.util.valid.ListValid;
import lombok.*;

import java.util.List;

@Builder
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class S3FilesReqDTO {
    @ListValid(errorCode = ErrorCode.FILE_UPLOAD_LENGTH_MISSING, maxLength = 5)
    private List<String> fileNames;
    @DynamicValid(errorCode = ErrorCode.FILE_UPLOAD_TYPE_NOT_MATCH,pattern = Regex.FILE_TYPE)
    private String fileType; // 이미지, PDF등 구분
}
