package com.talearnt.post.service;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.post.exchange.ExchangePostMapper;
import com.talearnt.post.exchange.repository.ExchangePostCustomRepository;
import com.talearnt.post.exchange.request.ExchangePostReqDTO;
import com.talearnt.post.exchange.repository.ExchangePostRepository;
import com.talearnt.post.exchange.entity.ExchangePost;
import com.talearnt.post.exchange.response.ExchangePostListResDTO;
import com.talearnt.post.exchange.response.ExchangePostReadResDTO;
import com.talearnt.util.common.PageUtil;
import com.talearnt.util.common.Pagination;
import com.talearnt.util.common.UserUtil;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.jwt.UserInfo;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.response.PaginatedResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class ExchangePostServiceImpl implements PostService<ExchangePostReqDTO> {

    private final ExchangePostRepository exchangePostRepository;
    private final ExchangePostCustomRepository exchangePostCustomRepository;

    @Override
    public ResponseEntity<PaginatedResponse<List<ExchangePostListResDTO>>> showList(Integer page) {
        log.info("재능교 교환 게시글 목록 가져오기 시작 : {}", page);
        //페이지 번호가 0보다 작을 경우 PAGE_MIN_NUMBER 발생
        PageUtil.validateMinPageNo(page);

        //페이지된 리스트 항목 가져오기
        Page<ExchangePost> exchangePostList = exchangePostRepository
                .findPageBy(PageRequest.of(page - 1, 20, Sort.by(Sort.Direction.DESC, "createdAt")));

        //Pagination으로 변환
        Pagination pagination = PageUtil.separatePaginationFromEntity(exchangePostList);

        //페이지 번호가 최대 페이지 번호를 초과할 경우 PAGE_OVER_MAX_NUMBER Exception 발생
        PageUtil.validateMaxPageNo(pagination);

        log.info("재능교 교환 게시글 목록 가져오기 끝");
        //List<ExchangePostListResDTO> 형태로 변환하여 반환
        return PaginatedResponse.success(ExchangePostMapper.INSTANCE.toListWithExchangePostListResDTOList(exchangePostList.getContent()), pagination);
    }

    @Override
    public ResponseEntity<CommonResponse<String>> create(ExchangePostReqDTO createDTO) {
        log.info("Exchange Post Create 시작 : {}", createDTO);

        //유저 정보 확인 ( JWT 토큰을 가지고 있지 않은데 생성하려는 경우 막음 )
        UserUtil.validateUserInfo(createDTO.getUserInfo());

        //DTO -> Entity로 변환
        ExchangePost entity = ExchangePostMapper.INSTANCE.toEntity(createDTO);

        //Data 저장
        exchangePostRepository.save(entity);

        log.info("Exchange Post Create 끝");
        return CommonResponse.success("재능 교환 게시글을 등록했습니다.");
    }

    @Override
    @Transactional
    public ResponseEntity<CommonResponse<ExchangePostReadResDTO>> read(Long id) {
        log.info("Exchagne Post Read 시작 : {}", id);

        // 해당 게시글 조회
        ExchangePost exchangePost = exchangePostRepository.findByExchangePostNoAndDeletedAtIsNull(id)
                .orElseThrow(() -> new CustomRuntimeException(ErrorCode.POST_NOT_FOUND));

        //조회 수 +1 업데이트
        exchangePost.setCount(exchangePost.getCount()+1);

        log.info("Exchagne Post Read 끝");
        return CommonResponse.success(ExchangePostMapper.INSTANCE.toExchangePostReadResDTO(exchangePost));
    }

    @Override
    public ResponseEntity<CommonResponse<String>> update(ExchangePostReqDTO updateDTO, Long id) {
        log.info("Exchange Post Update 시작 : {}", updateDTO);

        //Update 할 데이터 DB에 있는 지 조회 없으면 게시글 찾을 수 없음 Exception
        ExchangePost exchangePost = exchangePostRepository.findById(id)
                .orElseThrow(() -> new CustomRuntimeException(ErrorCode.POST_NOT_FOUND));

        //DB의 User No 와 JWT의 User No가 같지 않으면 게시글 조작 권한 없음.
        if (exchangePost.getUser().getUserNo() != updateDTO.getUserInfo().getUserNo()) {
            throw new CustomRuntimeException(ErrorCode.POST_ACCESS_DENIED);
        }

        //Exchange Post Entity로 변환
        ExchangePost willChangeExchangePost = ExchangePostMapper.INSTANCE.toUpdateEntity(updateDTO, exchangePost);

        //DB 업데이트 진행
        exchangePostRepository.save(willChangeExchangePost);

        log.info("Exchange Post Update 끝");
        return CommonResponse.success("재능 교환 게시글을 수정했습니다.");
    }

    @Override
    public ResponseEntity<CommonResponse<String>> delete(Long targetId, Authentication authentication) {
        log.info("Exchange Post Delete 시작 : {}", targetId);

        //JWT 인증 정보 확인
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();
        UserUtil.validateUserInfo(userInfo);

        // 게시글 존재 여부 확인
        if (!exchangePostRepository.existsById(targetId)) {
            throw new CustomRuntimeException(ErrorCode.POST_NOT_FOUND);
        }

        //JWT 유저 No와 DB 유저 No가 같으면 삭제
        if (exchangePostRepository.deleteByPostIdAndUserNo(targetId, userInfo.getUserNo()) == 0) {
            throw new CustomRuntimeException(ErrorCode.POST_ACCESS_DENIED);
        }

        log.info("Exchange Post Delete 끝");
        return CommonResponse.success("재능 교환 게시글을 삭제했습니다.");
    }

}
