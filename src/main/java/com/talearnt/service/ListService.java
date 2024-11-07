package com.talearnt.service;

import com.talearnt.util.response.PaginatedResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ListService<T, R> {
    ResponseEntity<PaginatedResponse<List<R>>> showList(T page);
}
