package com.talearnt.post.favorite;

import com.talearnt.post.exchange.response.ExchangePostListResDTO;
import com.talearnt.util.common.ClientPath;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.response.PaginatedResponse;
import com.talearnt.util.version.RestControllerV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestControllerV1
@RequiredArgsConstructor
@Log4j2
public class FavoriteController implements FavoriteApi {

    private final FavoriteService favoriteService;

    //찜 게시글 목록
    @GetMapping("/posts/exchanges/favorites")
    public ResponseEntity<CommonResponse<PaginatedResponse<List<ExchangePostListResDTO>>>> getFavoriteExchanges(@RequestParam(required = false, defaultValue = "1") String page,
                                                                                                                @RequestParam(required = false, defaultValue = "15") String size,
                                                                                                                @ClientPath String path,
                                                                                                                Authentication auth) {
        return null;
    }


    //찜 게시글 추가/삭제
    @PostMapping("/posts/exchanges/{postNo}/favorite")
    public ResponseEntity<CommonResponse<Void>> favoriteExchangePost(@PathVariable Long postNo, Authentication auth){
        favoriteService.favoriteExchangePost(postNo, auth);
        return CommonResponse.success(null);
    }

}
