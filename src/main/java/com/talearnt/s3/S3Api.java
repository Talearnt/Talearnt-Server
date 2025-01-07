package com.talearnt.s3;

import com.talearnt.s3.request.S3FilesReqDTO;
import com.talearnt.util.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface S3Api {


    @Operation(summary = "S3 파일 업로드",
            description = "<h2>내용</h2>" +
                    "<p>S3 파일 업로드 입니다. Presigned URL 방식을 채택하고 있습니다.</p>" +
                    "<p>S3에 파일을 업로드할 수 있는 경로를 반환합니다.</p>" +
                    "<p>이 경로로 파일을 담아 PUT 요청을 보내야합니다.</p>" +
                    "<h2>Request</h2>" +
                    "<ul>" +
                        "<li>fileNames : 파일 이름 </li>" +
                        "<li>fileType : 파일 타입 - images, documents</li>" +
                    "</ul>" +
                    "<p>PDF 파일을 업로드할 경우 파일 타입을 documents로 보내주시길 바랍니다.</p>" +
                    "<p>그 외 이미지 파일은 images로 보내주시면 됩니다.</p>" +
                    "<p>이렇게 구분지은 이유는 S3에서 path로 따로 관리하기 위함입니다.</p>" +
                    "<p>이미지와 PDF가 같이 있을 경우는 막아주시면 좋겠지만 이 부분은 이야기해서 객체마다 fileType을 받을지 의논 나눠봅시다.</p>" +
                    "<h2>Response</h2>" +
                    "<p>Presigned URL : 이미지 업로드 할 수 있는 경로 - 유지 기간 3분</p>" +
                    "<h2>CORS 문제</h2>" +
                    "<p>S3에서 업로드할 때 CORS 문제가 발생할 수 있습니다.</p>"+
                    "<p>개발 단계에서는 localhost를 허용하지만 운영 단계에서는 제거해야합니다.</p>" +
                    "<p>그래서 운영 단계에서 허용할 도메인 주소 OR 외부에서 접속 가능한 고정 아이피가 필요합니다.</p>" +
                    "<p>업로드 개발이 완벽히 끝날 경우 Localhost를 허용하지 않겠습니다.</p>"
    )
    public ResponseEntity<CommonResponse<List<String>>> generatePresignedUrl(@RequestBody S3FilesReqDTO images);

}
