package com.talearnt.post.exchange;


import com.talearnt.examples.RestControllerV1;
import com.talearnt.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestControllerV1
@Log4j2
@RequiredArgsConstructor
public class ExchangePostController {

    private final PostService postService;

    @PostMapping("/exchage-posts")
    public void addPost(@RequestBody ExchangePostCreateReqDTO dto){
        postService.createPost(dto);
    }

}
