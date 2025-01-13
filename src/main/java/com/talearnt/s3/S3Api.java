package com.talearnt.s3;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.s3.request.S3FilesReqDTO;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.valid.ListValid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface S3Api {


    @Operation(summary = "S3 파일 업로드",
            description = "<h2>내용</h2>" +
                    "<p>S3 파일 업로드 입니다. Presigned URL 방식을 채택하고 있습니다.</p>" +
                    "<p>S3에 파일을 업로드할 수 있는 경로를 반환합니다.</p>" +
                    "<p>이 경로로 파일을 담아 PUT 요청을 보내야합니다.</p>" +
                    "<hr/>" +
                    "<h2>Request</h2>" +
                    "<ul>" +
                        "<li>fileNames : 파일 이름 </li>" +
                        "<li>fileType : 파일 타입 - image/jpeg, application/pdf 등등</li>" +
                        "<li>fileSize : 파일 사이즈 </li>" +
                    "</ul>" +
                    "<p>fileTyle은 file의 Content-tpye을 보내주시길 바랍니다.</p>" +
                    "<p>application/pdf, image/jpeg 등등</p>" +
                    "<p>이렇게 구분지은 이유는 S3에서 path로 따로 관리하고, URL 옵션을 걸기 위함입니다.</p>" +
                    "<p>File Size도 같이 보내는 이유는 Server에서도 Presigned URL 만들기 전 한 번 더 검사하기 위함입니다.</p>" +
                    "<p>PUT 요청을 보낼 때 content-length, content-tpye도 포함해서 보내셔야 합니다.</p>" +
                    "<hr/>" +
                    "<h2>Response</h2>" +
                    "<p>Presigned URLs : 이미지 업로드 할 수 있는 경로 - 유지 기간 3분</p>" +
                    "<hr/>" +
                    "<h2>CORS 문제</h2>" +
                    "<p>S3에서 업로드할 때 CORS 문제가 발생할 수 있습니다.</p>"+
                    "<p>개발 단계에서는 localhost를 허용하지만 운영 단계에서는 제거해야합니다.</p>" +
                    "<p>그래서 운영 단계에서 허용할 도메인 주소 OR 외부에서 접속 가능한 고정 아이피가 필요합니다.</p>" +
                    "<p>업로드 개발이 완벽히 끝날 경우 Localhost를 허용하지 않겠습니다.</p>"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", ref = "FILE_UPLOAD_TYPE_NOT_MATCH"),
            @ApiResponse(responseCode = "415", ref = "FILE_UPLOAD_EXTENSION_MISMATCH"),
            @ApiResponse(responseCode = "413", ref = "FILE_UPLOAD_SIZE_OVER"),
            @ApiResponse(responseCode = "401", ref = "EXPIRED_TOKEN"),
    })
    public ResponseEntity<CommonResponse<List<String>>> generatePresignedUrl(@RequestBody @Valid @ListValid(errorCode = ErrorCode.FILE_UPLOAD_LENGTH_MISSING, maxLength = 5) List<S3FilesReqDTO> images, Authentication auth);

}
