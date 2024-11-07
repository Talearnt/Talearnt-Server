package com.talearnt.util.response;


import com.talearnt.util.common.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "해당 페이지 목록 가져올 때 사용할 CommonResponse")
public class PaginatedResponse<T> {
    @Schema(description = "실제 데이터")
    private T data;

    @Schema(description = "페이지에 대한 정보")
    private Pagination pagination;

    public static <T> ResponseEntity<PaginatedResponse<T>> success(T data, Pagination pagination) {
        return ResponseEntity.ok(new PaginatedResponse<T>(data, pagination));
    }
}
