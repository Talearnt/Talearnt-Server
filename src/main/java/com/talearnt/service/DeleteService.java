package com.talearnt.service;

import com.talearnt.util.response.CommonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface DeleteService<T, R> {
    ResponseEntity<CommonResponse<R>> delete(T targetId, Authentication authentication);
}
