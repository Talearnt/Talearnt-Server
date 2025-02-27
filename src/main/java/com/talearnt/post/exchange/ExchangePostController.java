package com.talearnt.post.exchange;


import com.talearnt.post.exchange.request.ExchangePostReqDTO;
import com.talearnt.post.exchange.response.ExchangePostDetailResDTO;
import com.talearnt.post.exchange.response.ExchangePostListResDTO;
import com.talearnt.user.talent.MyTalentService;
import com.talearnt.user.talent.response.MyTalentsResDTO;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.response.PaginatedResponse;
import com.talearnt.util.version.RestControllerV1;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Post-Exchange")
@RestControllerV1
@Log4j2
@RequiredArgsConstructor
public class ExchangePostController implements ExchangePostApi{

    private final MyTalentService myTalentService;
    private final ExchangePostService exchangePostService;

    //게시글 주고 싶은 재능 불러오기
    @GetMapping("/posts/exchange/talents/offered")
    public ResponseEntity<CommonResponse<List<MyTalentsResDTO>>> getWantGiveMyTalentsForPost(Authentication auth){
        return CommonResponse.success(myTalentService.getMyGiveTalents(auth));
    }

    //게시글 상세보기
    @GetMapping("/posts/exchanges/{postNo}")
    public ResponseEntity<CommonResponse<ExchangePostDetailResDTO>> getExchangePostDetail(@PathVariable Long postNo, Authentication auth){
        return CommonResponse.success(exchangePostService.getExchangePostDetail(postNo,auth));
    }

    //게시글 목록
    @GetMapping("/posts/exchanges")
    public ResponseEntity<CommonResponse<PaginatedResponse<List<ExchangePostListResDTO>>>> getExchangePostList(@RequestParam(value = "giveTalents",required = false,defaultValue = "")  List<String> giveTalents,//Integer로 변환 필요
                                                                                            @RequestParam(value = "receiveTalents",required = false,defaultValue = "") List<String> receiveTalents,//Integer로 변환 필요
                                                                                            @RequestParam(value = "order", required = false,defaultValue = "recent") String order,//recent,popular 로 변환 필요
                                                                                            @RequestParam(value = "duration",required = false) String duration,// 이상한 값이 넘어올 경우 duration 없이 조건
                                                                                            @RequestParam(value = "type", required = false) String type, //ExchangeType으로 변환 필요, ExchangeType 으로 변환 실패 시 null로 변환
                                                                                            @RequestParam(value = "badge",required = false) String requiredBadge, // Boolean 값으로 넘어오지 않을 경우 null로 변환
                                                                                            @RequestParam(value = "status",required = false) String status, //ExchangePostStatus으로 변환 필요, ExchangePostStatus 으로 변환 실패시  으로 변환 실패 시 null로 변환
                                                                                            @RequestParam(value = "page",required = false,defaultValue = "1") String page,
                                                                                            @RequestParam(value = "size",required = false,defaultValue = "15") String size,
                                                                                            @RequestParam(value = "lastNo", required = false) String lastNo,
                                                                                            @RequestParam(value = "search",required = false) String search,
                                                                                            Authentication auth){
        return CommonResponse.success(exchangePostService.getExchangePostList(giveTalents,receiveTalents,order,duration,type,requiredBadge,status,page,size,search, lastNo, auth));
    }

    //게시글 작성
    @PostMapping("/posts/exchanges")
    public ResponseEntity<CommonResponse<Long>> writeExchangePost(@RequestBody @Valid ExchangePostReqDTO exchangePostReqDTO){
        return CommonResponse.success(exchangePostService.writeExchangePost(exchangePostReqDTO));
    }


    //게시글 수정
    @PutMapping("/posts/exchanges/{postNo}")
    public ResponseEntity<CommonResponse<Void>> updateExchangePost(@PathVariable Long postNo, @RequestBody @Valid ExchangePostReqDTO exchangePostReqDTO){
        return CommonResponse.success(exchangePostService.updateExchangePost(postNo,exchangePostReqDTO));
    }

    //게시글 삭제
    @DeleteMapping("/posts/exchanges/{postNo}")
    public ResponseEntity<CommonResponse<String>> deleteExchangePost(@PathVariable Long postNo, Authentication auth){
        return CommonResponse.success(exchangePostService.deleteExchangePost(postNo, auth));
    }

}
