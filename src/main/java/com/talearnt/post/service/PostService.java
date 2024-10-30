package com.talearnt.post.service;

import com.talearnt.util.response.CommonResponse;
import org.springframework.http.ResponseEntity;

public interface PostService {
    ResponseEntity<CommonResponse> addPost();
}
