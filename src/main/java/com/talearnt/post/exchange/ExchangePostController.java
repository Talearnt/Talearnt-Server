package com.talearnt.post.exchange;


import com.talearnt.examples.RestControllerV1;
import com.talearnt.post.exchange.request.ExchangePostCreateReqDTO;
import com.talearnt.post.service.PostService;
import com.talearnt.util.common.RequiredJwtValueDTO;
import com.talearnt.util.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestControllerV1
@Log4j2
@RequiredArgsConstructor
public class ExchangePostController {

    private final PostService postService;

    @PostMapping("/exchage-posts")
    public ResponseEntity<CommonResponse<String>> addPost(@RequestBody ExchangePostCreateReqDTO dto){
        return postService.createPost(dto);
    }

}
