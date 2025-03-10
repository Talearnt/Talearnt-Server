package com.talearnt.util.pagination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/** Pagination Repository -> Service Layer로 이동할 때 감싸기 위한 클래스이다.*/
public class PagedListWrapper<T> {
    private List<T> list;
    private PagedData pagedData;
}
