package com.talearnt.examples;


import com.talearnt.enums.ErrorCode;
import com.talearnt.util.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "example", description = "Swagger 사용 예제입니다.")
@RestControllerV1
@RequiredArgsConstructor
public class ExampleController {

    @GetMapping("/exam")
    @Operation(summary = "내용에 관한 요약은 여기에 적습니다",
            description = "내용에 대한 설명은 여기에 적습니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적"),
                    @ApiResponse(responseCode = "400", description = "실패 로맨틱"),
                    @ApiResponse(responseCode = "500", description = "git-01")
            })

    public ResponseEntity<CommonResponse<List<ExamResDTO>>> getExams(){

        List<ExamResDTO> examList = new ArrayList<>();

        examList.add(new ExamResDTO.ExamResDTOBuilder().examId("예제 게시글 1").nickname("예제 닉네임1").build());
        examList.add(new ExamResDTO.ExamResDTOBuilder().examId("예제 게시글 2").nickname("예제 닉네임2").build());

        return CommonResponse.success(examList);
    }


    @PostMapping("/exam")
    @Operation(summary = "예제 수정 요약",
            description = "예제를 수정하는 내용입니다",
    responses = {
            @ApiResponse(responseCode = "200", description = "성공적으로 예제의 정보를 바꿨습니다."),
            @ApiResponse(responseCode = "404", ref = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "403", ref = "USER_SUSPENDED"),
            @ApiResponse(responseCode = "400", ref = "DUPLICATE_USER_ID"),
            @ApiResponse(responseCode = "500", ref = "DB_CONNECTION_ERROR"),
            @ApiResponse(responseCode = "400", ref = "BAD_REQUEST"),
            @ApiResponse(responseCode = "402", ref = "?????")
    })
    public ResponseEntity<CommonResponse<ExamResDTO>> updateExam(@RequestBody ExamReqDTO dto){

        return null;
    }

}
