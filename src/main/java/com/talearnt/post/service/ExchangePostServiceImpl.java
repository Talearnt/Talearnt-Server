package com.talearnt.post.service;

import com.talearnt.post.exchange.ExchangePostCreateReqDTO;
import com.talearnt.post.exchange.ExchangePostRepository;
import com.talearnt.post.exchange.ExchangePostUpdateReqDTO;
import com.talearnt.post.exchange.entity.ExchangePost;
import com.talearnt.util.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Log4j2
public class ExchangePostServiceImpl implements PostService {

    private final ExchangePostRepository exchangePostRepository;
    private final ModelMapper mapper;


    @Override
    public ResponseEntity<CommonResponse> createPost(ExchangePostCreateReqDTO exchangePostReqDTO) {

        //DTO -> Server Data로 변환
        ExchangePost entity = mapper.map(exchangePostReqDTO,ExchangePost.class);
        log.info("DTO -> Entity로 변환된 값 : {}",entity);

        //Data 저장
        log.info("값 저장 : ",exchangePostRepository.save(entity));


        return null;
    }
}
