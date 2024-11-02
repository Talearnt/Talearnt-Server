package com.talearnt.post.service;

import com.talearnt.join.User;
import com.talearnt.post.exchange.request.ExchangePostCreateReqDTO;
import com.talearnt.post.exchange.ExchangePostRepository;
import com.talearnt.post.exchange.entity.ExchangePost;
import com.talearnt.post.exchange.response.ExchangePostReadResDTO;
import com.talearnt.util.common.UserUtil;
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
    public ResponseEntity<CommonResponse<String>> createPost(ExchangePostCreateReqDTO exchangePostReqDTO) {
        log.info("DTO 안 UserInfo 값 : {}",exchangePostReqDTO.getUserInfo());
        log.info("DTO 값 : {}",exchangePostReqDTO);

        //DTO -> Server Data로 변환
        ExchangePost entity = mapper.map(exchangePostReqDTO,ExchangePost.class);
        log.info("DTO -> Entity로 변환된 값 : {}",entity);

        //User Entity에 UserNo 설정
        entity.setUser(UserUtil.createUser(exchangePostReqDTO.getUserInfo().getUserNo()));

        log.info("UserNo 주입 후 값 : {}",entity);

        //Data 저장
        exchangePostRepository.save(entity);


        return CommonResponse.success("재능 교환 게시글을 등록했습니다.");
    }
}
