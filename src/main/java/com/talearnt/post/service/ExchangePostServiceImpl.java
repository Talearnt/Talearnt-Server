package com.talearnt.post.service;

import com.talearnt.enums.ErrorCode;
import com.talearnt.enums.post.ExchangePostStatus;
import com.talearnt.post.exchange.request.ExchangePostReqDTO;
import com.talearnt.post.exchange.ExchangePostRepository;
import com.talearnt.post.exchange.entity.ExchangePost;
import com.talearnt.post.exchange.response.ExchangePostReadResDTO;
import com.talearnt.util.common.UserUtil;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Log4j2
public class ExchangePostServiceImpl implements PostService<ExchangePostReqDTO>{

    private final ExchangePostRepository exchangePostRepository;
    private final ModelMapper mapper;


    @Override
    public ResponseEntity<CommonResponse<String>> create(ExchangePostReqDTO createDTO) {

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
    public ResponseEntity<CommonResponse<ExchangePostReadResDTO>> read(Long id) {
        log.info("Exchagne Post Read 시작 : {}", id);

        // 해당 게시글 조회
        Optional<ExchangePost> exchangePost = exchangePostRepository.findById(id);
        // 게시글 조회 실패 시 Exception 발생
        if (!exchangePost.isPresent()){
            throw new CustomRuntimeException(ErrorCode.POST_NOT_FOUND);
        }

        //게시글 Res DTO로 변환
        ExchangePostReadResDTO result = mapper.map(exchangePost.get(),ExchangePostReadResDTO.class);

        log.info("Exchagne Post Read 끝 : {}", result);
        return CommonResponse.success(result);
    }

    @Override
    public ResponseEntity<CommonResponse<String>> update(ExchangePostReqDTO updateDTO, Long id) {
        log.info("Exchange Post Update 시작 : {}",updateDTO);

        //Exchange Post Entity로 변환
        ExchangePost exchangePost = mapper.map(updateDTO,ExchangePost.class);

        //Exchange Post 안 User Setting
        exchangePost.setUser(UserUtil.createUser(updateDTO.getUserInfo()));

        //Exchage Post 의 Status 가 Null이면 모집중으로 설정
        if (exchangePost.getStatus() == null){
            exchangePost.setStatus(ExchangePostStatus.모집중);
        }

        //Exchange Post 변경할 PostNo 셋팅
        exchangePost.setExchangePostNo(id);

        //DB 업데이트 진행
        exchangePostRepository.save(exchangePost);

        log.info("Exchange Post Update 끝");
        return null;
    }
}
