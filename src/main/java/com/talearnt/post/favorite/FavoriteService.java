package com.talearnt.post.favorite;


import com.talearnt.enums.common.ErrorCode;
import com.talearnt.post.exchange.entity.ExchangePost;
import com.talearnt.post.exchange.repository.ExchangePostRepository;
import com.talearnt.post.exchange.response.ExchangePostListResDTO;
import com.talearnt.post.favorite.entity.FavoriteExchangePost;
import com.talearnt.post.favorite.repository.FavoriteExchagePostQueryRepository;
import com.talearnt.post.favorite.repository.FavoriteExchangePostRepository;
import com.talearnt.post.favorite.request.FavoriteSearchCondition;
import com.talearnt.util.common.PageUtil;
import com.talearnt.util.common.PostUtil;
import com.talearnt.util.common.UserUtil;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.filter.UserRequestLimiter;
import com.talearnt.util.jwt.UserInfo;
import com.talearnt.util.log.LogRunningTime;
import com.talearnt.util.pagination.PagedListWrapper;
import com.talearnt.util.response.PaginatedResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class FavoriteService {


    private final ExchangePostRepository exchangePostRepository;
    private final FavoriteExchangePostRepository favoriteExchangePostRepository;
    private final FavoriteExchagePostQueryRepository favoriteExchagePostQueryRepository;


    @LogRunningTime
    public PaginatedResponse<List<ExchangePostListResDTO>> getFavoriteExchanges(String path, String page, String size,Authentication auth) {
        log.info("찜 게시글 목록 조회 시작 - path: {}, page: {}, size: {}", path, page, size);

        //로그인 여부 확인
        UserInfo userInfo = UserUtil.validateAuthentication("찜 게시글 목록 조회 실패 - 로그인 안함 : {}", auth);

        //서치 컨디션 생성
        FavoriteSearchCondition condition = FavoriteSearchCondition
                .builder()
                .page(page)
                .size(size)
                .build();

        if ("web".equalsIgnoreCase(path)) {
            //웹 찜 게시글 목록 조회
            PagedListWrapper<ExchangePostListResDTO> wrapper = favoriteExchagePostQueryRepository.getFavoriteExchangePostsToWeb(userInfo.getUserNo(), condition);
            //페이지 정보로 변환
            Page<ExchangePostListResDTO> result = new PageImpl<>(wrapper.getList(), condition.getPage(),wrapper.getPagedData().getTotal());

            log.info("찜 게시글 목록 조회 끝 - 웹");
            //반환
            return new PaginatedResponse<>(result.getContent(), PageUtil.separatePaginationFromEntityToWeb(result, wrapper.getPagedData().getLatestCreatedAt()));
        }


        Page<ExchangePostListResDTO> result = favoriteExchagePostQueryRepository.getFavoriteExchangePostsToMobile(userInfo.getUserNo(), condition);
        log.info("찜 게시글 목록 조회 끝 - 모바일");
        return new PaginatedResponse<>(result.getContent(), PageUtil.separatePaginationFromEntityToMobile(result));
    }



    /**
     * 재능 교환 게시글 찜하기 시작 (Toggle) <br>
     * 조건 )<br>
     * - 로그인이 되어 있는가?<br>
     * - 게시글이 존재하는가?<br>
     * - 삭제된 게시글이 아닌가?
     *
     * @param postNo 재능교환 게시글 번호
     * @param auth   유저 Authentication
     */
    @Async
    @Transactional
    @LogRunningTime
    public void favoriteExchangePost(Long postNo, boolean favoriteStatus, UserInfo userInfo){
        log.info("재능 교환 게시글 찜하기 시작 - {}",postNo);


        //게시글이 존재하는가?
        ExchangePost exchangePost = exchangePostRepository.findById(postNo)
                .orElseThrow(()->{
                    log.error("재능 교환 게시글 찜하기 실패 - 해당 게시글 없음 : {}", ErrorCode.POST_NOT_FOUND);
                    return new CustomRuntimeException(ErrorCode.POST_NOT_FOUND);
                });

        //삭제된 게시글이 아닌가?
        if (exchangePost.getDeletedAt() != null){
            log.error("재능 교환 게시글 찜하기 실패 - 삭제된 게시글 : {}",ErrorCode.POST_NOT_FOUND);
            throw new CustomRuntimeException(ErrorCode.POST_NOT_FOUND);
        }

        //찜 게시글 등록 여부 확인
        FavoriteExchangePost favoriteExchangePost = favoriteExchagePostQueryRepository.findByPostNoAndUserId(postNo, userInfo.getUserNo())
                .orElse(null);

        //찜 게시글이 존재하는 경우
        if(favoriteExchangePost != null){
            //삭제되었는지 확인                      삭제가 되지 않았고                      false라면          삭제시간 현재시간 대입 : null 대입
            LocalDateTime deletedAt = favoriteExchangePost.getDeletedAt() == null && !favoriteStatus? LocalDateTime.now() : null;
            favoriteExchangePost.setDeletedAt(deletedAt);
            //삭제가 안되었다면 현재시간 대입 -> 새로운 찜 게시글인 것처럼 느껴지도록
            if (deletedAt == null) favoriteExchangePost.setCreatedAt(LocalDateTime.now());
        }
        //찜 게시글이 존재하지 않은 경우
        else{
            favoriteExchangePost = new FavoriteExchangePost(null, exchangePost.getExchangePostNo(), userInfo.getUserNo(), null, null);
        }

        //찜 게시글 추가 / 수정
        favoriteExchangePostRepository.save(favoriteExchangePost);

        log.info("재능 교환 게시글 찜하기 끝");
    }


}
