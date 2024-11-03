package com.talearnt.service;

import com.talearnt.util.response.CommonResponse;
import org.springframework.http.ResponseEntity;

public interface DeleteService<T, R> {
    ResponseEntity<CommonResponse<R>> delete(T deleteDTO);
}
