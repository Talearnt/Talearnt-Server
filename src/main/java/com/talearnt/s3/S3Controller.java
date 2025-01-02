package com.talearnt.s3;

import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.version.RestControllerV1;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@RestControllerV1
@Log4j2
public class S3Controller {


    public ResponseEntity<CommonResponse<String>> generatePresignedUrl(@RequestParam String fileName){ //파일 이름은 인코딩해서 보낼 것

        return null;
    }

}
