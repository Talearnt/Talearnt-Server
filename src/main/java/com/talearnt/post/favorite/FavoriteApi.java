package com.talearnt.post.favorite;

import com.talearnt.enums.common.ClientPathType;
import com.talearnt.post.exchange.response.ExchangePostListResDTO;
import com.talearnt.util.common.ClientPath;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.response.PaginatedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface FavoriteApi {

    @Operation(summary = "내가 찜한 게시글 목록",
            tags = {"Post-Exchange"},
            description = "<h2>내용</h2>" +
                    "<p>구현중입니다</p>")
    public ResponseEntity<CommonResponse<PaginatedResponse<List<ExchangePostListResDTO>>>> getFavoriteExchanges(@RequestParam(required = false, defaultValue = "1") String page,
                                                                                                                @RequestParam(required = false, defaultValue = "15") String size,
                                                                                                                @ClientPath ClientPathType path,
                                                                                                                Authentication auth);



    @Operation(summary = "재능 교환 게시글 찜하기",
            tags = {"Post-Exchange"},
            description = "<h2>내용</h2>" +
                    "<p>재능 교환 게시글 찜하기입니다. 토글 방식으로 작동합니다.</p>" +
                    "<p>비동기 방식으로 작동하기 때문에 좋아요 버튼을 눌렀을 경우 1~2초간 서버에 요청 보내는 것을 제어해야 합니다.</p>" +
                    "<p>제어하지 않을 경우 서버에 부화가 생겨 문제가 발생할 수 있습니다.</p>" +
                    "<p>재능 교환 게시글 찜은 반환값이 없습니다.</p>" +
                    "<p>찜 갯수를 반환할까도 싶었지만, 실시간으로 좋아요 변동이 필요하지 않을 것이라 판단해 프론트 쪽에서 좋아요 갯수를 제어해주시면 될 것 같습니다.</p>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", ref = "EXPIRED_TOKEN"),
            @ApiResponse(responseCode = "404", ref = "POST_NOT_FOUND")
    })
    public ResponseEntity<CommonResponse<Void>> favoriteExchangePost(@PathVariable Long postNo, Authentication auth);
}
