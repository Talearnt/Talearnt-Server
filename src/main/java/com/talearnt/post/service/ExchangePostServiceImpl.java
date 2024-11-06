package com.talearnt.post.service;

import com.talearnt.enums.ErrorCode;
import com.talearnt.post.exchange.ExchangePostMapper;
import com.talearnt.post.exchange.request.ExchangePostReqDTO;
import com.talearnt.post.exchange.ExchangePostRepository;
import com.talearnt.post.exchange.entity.ExchangePost;
import com.talearnt.post.exchange.response.ExchangePostReadResDTO;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class ExchangePostServiceImpl implements PostService<ExchangePostReqDTO>{

    private final ExchangePostRepository exchangePostRepository;

    @Override
    public ResponseEntity<CommonResponse<String>> create(ExchangePostReqDTO createDTO) {
        log.info("Exchange Post Create 시작 : {}",createDTO);

        //DTO -> Entity로 변환
        ExchangePost entity = ExchangePostMapper.INSTANCE.toEntity(createDTO);

        //Data 저장
        exchangePostRepository.save(entity);

        log.info("Exchange Post Create 끝");
        return CommonResponse.success("재능 교환 게시글을 등록했습니다.");
    }

    @Override
    public ResponseEntity<CommonResponse<ExchangePostReadResDTO>> read(Long id) {
        log.info("Exchagne Post Read 시작 : {}", id);

        // 해당 게시글 조회
        ExchangePost exchangePost = exchangePostRepository.findById(id)
                .orElseThrow(()-> new CustomRuntimeException(ErrorCode.POST_NOT_FOUND));

        log.info("Exchagne Post Read 끝");
        return CommonResponse.success(ExchangePostMapper.INSTANCE.toExchangePostReadResDTO(exchangePost));
    }

    @Override
    public ResponseEntity<CommonResponse<String>> update(ExchangePostReqDTO updateDTO, Long id) {
        log.info("Exchange Post Update 시작 : {}",updateDTO);

        //Update 할 데이터 DB에 있는 지 조회
        ExchangePost isExchangePost = exchangePostRepository.findById(id)
                .orElseThrow(()-> new CustomRuntimeException(ErrorCode.POST_NOT_FOUND));

        //Exchange Post Entity로 변환
        ExchangePost exchangePost = ExchangePostMapper.INSTANCE.toUpdateEntity(updateDTO,isExchangePost);

        //DB 업데이트 진행
        exchangePostRepository.save(exchangePost);

        log.info("Exchange Post Update 끝");
        return CommonResponse.success("재능 교환 게시글을 수정했습니다.");
    }
}
