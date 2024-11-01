package com.talearnt.post.service;

import com.talearnt.post.exchange.request.ExchangePostCreateReqDTO;
import com.talearnt.post.exchange.response.ExchangePostReadResDTO;
import com.talearnt.util.response.CommonResponse;
import org.springframework.http.ResponseEntity;

public interface PostService {
    ResponseEntity<CommonResponse<String>> createPost(ExchangePostCreateReqDTO exchangePostReqDTO);
}
