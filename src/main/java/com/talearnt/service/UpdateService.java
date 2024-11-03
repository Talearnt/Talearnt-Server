package com.talearnt.service;

import com.talearnt.util.response.CommonResponse;
import org.springframework.http.ResponseEntity;

public interface UpdateService<T, R> {
    ResponseEntity<CommonResponse<R>> update(T updateDTO);
}
