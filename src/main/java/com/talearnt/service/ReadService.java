package com.talearnt.service;

import com.talearnt.util.response.CommonResponse;
import org.springframework.http.ResponseEntity;

public interface ReadService<T, R> {
    ResponseEntity<CommonResponse<R>> read(T readDTO);
}
