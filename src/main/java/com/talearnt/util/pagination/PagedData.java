package com.talearnt.util.pagination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
/** Pagination 할 때 필요한 정보와 데이터 등록/삭제 여부를 알기 위한 필드를 담은 클래스이다.*/
public class PagedData {
    private Long total; // 데이터 총 개수 -> pageImpl 사용
    private LocalDateTime latestCreatedAt; //가장 최근 등록된 데이터 일자
}
