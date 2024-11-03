package com.talearnt.service;

import com.talearnt.util.response.CommonResponse;
import org.springframework.http.ResponseEntity;

public interface CreateService<T, R> {
    ResponseEntity<CommonResponse<R>> create(T createDTO);
}
