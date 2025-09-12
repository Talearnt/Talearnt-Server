package com.talearnt.post.favorite;

import com.talearnt.enums.common.ClientPathType;
import com.talearnt.post.exchange.response.ExchangePostListResDTO;
import com.talearnt.post.favorite.request.FavoriteStatusReqDTO;
import com.talearnt.util.common.ClientPath;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.response.PaginatedResponse;
import com.talearnt.util.version.RestControllerV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestControllerV1
@RequiredArgsConstructor
@Log4j2
public class FavoriteController implements FavoriteApi {

    private final FavoriteService favoriteService;

    //찜 게시글 추가/삭제
    @PostMapping("/posts/exchanges/{postNo}/favorite")
    public ResponseEntity<CommonResponse<Void>> favoriteExchangePost(@PathVariable Long postNo, @RequestBody FavoriteStatusReqDTO favoriteStatusReqDTO){
        favoriteService.favoriteExchangePost(postNo, favoriteStatusReqDTO.isFavorite(),favoriteStatusReqDTO.getUserInfo());
        return CommonResponse.success(null);
    }

}
