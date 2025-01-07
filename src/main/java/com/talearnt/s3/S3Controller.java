package com.talearnt.s3;

import com.talearnt.s3.request.S3FilesReqDTO;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.version.RestControllerV1;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestControllerV1
@Log4j2
@RequiredArgsConstructor
@Tag(name = "Upload")
public class S3Controller implements S3Api {

    private final S3Service s3Service;


    @PostMapping("/uploads")
    public ResponseEntity<CommonResponse<List<String>>> generatePresignedUrl(@RequestBody S3FilesReqDTO images){ //파일 이름은 인코딩해서 보낼 것
        return CommonResponse.success(s3Service.generatePresignedUrls(images));
    }

}
