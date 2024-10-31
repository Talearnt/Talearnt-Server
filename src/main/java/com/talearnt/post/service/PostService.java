package com.talearnt.post.service;

import com.talearnt.post.exchange.ExchangePostCreateReqDTO;
import com.talearnt.post.exchange.ExchangePostUpdateReqDTO;
import com.talearnt.util.response.CommonResponse;
import org.springframework.http.ResponseEntity;

public interface PostService {
    ResponseEntity<CommonResponse> createPost(ExchangePostCreateReqDTO exchangePostReqDTO);
}
