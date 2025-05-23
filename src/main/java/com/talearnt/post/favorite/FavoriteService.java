package com.talearnt.post.favorite;


import com.talearnt.enums.common.ErrorCode;
import com.talearnt.post.exchange.entity.ExchangePost;
import com.talearnt.post.exchange.repository.ExchangePostRepository;
import com.talearnt.post.favorite.entity.FavoriteExchangePost;
import com.talearnt.post.favorite.repository.FavoriteExchagePostQueryRepository;
import com.talearnt.post.favorite.repository.FavoriteExchangePostRepository;
import com.talearnt.util.common.UserUtil;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.jwt.UserInfo;
import com.talearnt.util.log.LogRunningTime;
import com.talearnt.util.response.PaginatedResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
    public PaginatedResponse<List<FavoriteExchangePost>> getFavoriteExchanges(String path, String page, String size,Authentication auth) {
        log.info("찜 게시글 목록 조회 시작");

        //로그인 여부 확인
        UserInfo userInfo = UserUtil.validateAuthentication("찜 게시글 목록 조회 실패 - 로그인 안함 : {}", auth);



        return null;
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
    public void favoriteExchangePost(Long postNo, Authentication auth){
        log.info("재능 교환 게시글 찜하기 시작 - {}",postNo);

        //로그인 여부 확인
        UserInfo userInfo = UserUtil.validateAuthentication("재능교환 게시글 찜하기", auth);

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
            //삭제되었는지 확인
            LocalDateTime deletedAt = favoriteExchangePost.getDeletedAt() == null ? LocalDateTime.now() : null;
            favoriteExchangePost.setDeletedAt(deletedAt);
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
