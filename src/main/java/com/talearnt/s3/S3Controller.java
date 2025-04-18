package com.talearnt.s3;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.s3.request.S3FilesReqDTO;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.valid.ListValid;
import com.talearnt.util.version.RestControllerV1;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestControllerV1
@Log4j2
@RequiredArgsConstructor
@Tag(name = "Upload")
@Validated
public class S3Controller implements S3Api {

    private final S3Service s3Service;


    //Presigned URL 발급
    @PostMapping("/uploads")
    public ResponseEntity<CommonResponse<List<String>>> generatePresignedUrl(@RequestBody @Valid @ListValid(errorCode = ErrorCode.FILE_UPLOAD_LENGTH_MISSING, maxLength = 5) List<S3FilesReqDTO> images, Authentication auth){ //파일 이름은 인코딩해서 보낼 것
        return CommonResponse.success(s3Service.generatePresignedUrls(images,auth));
    }

}
