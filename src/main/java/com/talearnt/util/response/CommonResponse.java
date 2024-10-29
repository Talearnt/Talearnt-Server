package com.talearnt.util.response;

import com.talearnt.enums.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Log4j2
@Schema(description = "Common API Response")
public class CommonResponse<T> {
    @Schema(description = "성공 여부", example = "true")
    private boolean isSuccess;
    @Schema(description = "실제 데이터")
    private T data;
    @Schema(description = "에러 코드", example = "null")
    private String errorCode;
    @Schema(description = "에러 메시지", example = "null")
    private String errorMessage;


    public static <T> ResponseEntity<CommonResponse<T>> success(T data){
       return ResponseEntity.ok(new CommonResponse<T>(true, data,null,null));
    }

    public static <T> ResponseEntity<CommonResponse<T>> error(ErrorCode errorCode) {
        HttpStatus status = HttpStatus.valueOf(Integer.parseInt(errorCode.getCode().substring(0, 3)));
        log.error("에러 발생! {} : {} - {}",
                errorCode.name(),
                errorCode.getCode(),
                errorCode.getMessage());
        return ResponseEntity.status(status).body(new CommonResponse<>(false, null, errorCode.getCode(),errorCode.getMessage()));
    }

}
