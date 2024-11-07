package com.talearnt.util.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Pagination {
    @Schema(description = "다음 페이지 존재 유무 : boolean", example = "null")
    private boolean hasNext;
    @Schema(description = "이전 페이지 존재 유무 : boolean", example = "null")
    private boolean hasPrevious;
    @Schema(description = "총 페이지 : int", example = "1")
    private int totalPages;
    @Schema(description = "현재 페이지 : int", example = "1")
    private int currentPage;
}
