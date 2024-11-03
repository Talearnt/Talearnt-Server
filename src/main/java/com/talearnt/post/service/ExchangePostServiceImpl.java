package com.talearnt.post.service;

import com.talearnt.post.exchange.request.ExchangePostCreateReqDTO;
import com.talearnt.post.exchange.ExchangePostRepository;
import com.talearnt.post.exchange.entity.ExchangePost;
import com.talearnt.post.exchange.request.ExchangePostUpdateReqDTO;
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
public class ExchangePostServiceImpl implements PostService<ExchangePostCreateReqDTO,ExchangePostUpdateReqDTO>{

    private final ExchangePostRepository exchangePostRepository;
    private final ModelMapper mapper;


    @Override
    public ResponseEntity<CommonResponse<String>> create(ExchangePostCreateReqDTO createDTO) {

        log.info("DTO 안 UserInfo 값 : {}",createDTO.getUserInfo());
        log.info("DTO 값 : {}",createDTO);

        //DTO -> Server Data로 변환
        ExchangePost entity = mapper.map(createDTO,ExchangePost.class);
        log.info("DTO -> Entity로 변환된 값 : {}",entity);

        //User Entity에 UserNo 설정
        entity.setUser(UserUtil.createUser(createDTO.getUserInfo()));

        log.info("UserNo 주입 후 값 : {}",entity);

        //Data 저장
        exchangePostRepository.save(entity);

        return CommonResponse.success("재능 교환 게시글을 등록했습니다.");
    }

    @Override
    public ResponseEntity<CommonResponse<String>> update(ExchangePostUpdateReqDTO updateDTO) {
        return null;
    }
}
