package com.talearnt.util.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Pagination {
    @Schema(description = "다음 페이지 존재 유무 : boolean", example = "null")
    private Boolean hasNext;
    @Schema(description = "이전 페이지 존재 유무 : boolean", example = "null")
    private Boolean hasPrevious;
    @Schema(description = "게시글 총 개수 : int", example = "1")
    private Long totalCount;
    @Schema(description = "총 페이지 : int", example = "1")
    private Integer totalPages;
    @Schema(description = "현재 페이지 : int", example = "1")
    private Integer currentPage;
    @Schema(description = "가장 최근 데이터 등록일")
    private LocalDateTime latestCreatedAt;
}
