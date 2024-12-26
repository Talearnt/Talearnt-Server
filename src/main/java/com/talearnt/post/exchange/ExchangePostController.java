package com.talearnt.post.exchange;

import com.talearnt.enums.post.ExchangePostStatus;
import com.talearnt.enums.post.ExchangeType;
import com.talearnt.post.exchange.request.ExchangePostReqDTO;
import com.talearnt.post.exchange.response.ExchangePostListResDTO;
import com.talearnt.user.talent.MyTalentService;
import com.talearnt.user.talent.response.MyTalentsResDTO;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.version.RestControllerV1;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Post-Exchange")
@RestControllerV1
@Log4j2
@RequiredArgsConstructor
public class ExchangePostController implements ExchangePostApi{

    private final MyTalentService myTalentService;
    private final ExchangePostService exchangePostService;

    @GetMapping("/posts/exchange/talents/offered")
    public ResponseEntity<CommonResponse<List<MyTalentsResDTO>>> getWantGiveMyTalentsForPost(Authentication auth){
        return CommonResponse.success(myTalentService.getMyGiveTalents(auth));
    }


    @GetMapping("/posts/exchanges")
    public ResponseEntity<CommonResponse<List<ExchangePostListResDTO>>> getExchangePostList(@RequestParam(value = "categories",required = false,defaultValue = "") List<String> categories,//Integer로 변환 필요
                                                                                            @RequestParam(value = "talents",required = false,defaultValue = "") List<String> talents,//Integer로 변환 필요
                                                                                            @RequestParam(value = "order", required = false,defaultValue = "recent") String order,//recent,popular 로 변환 필요
                                                                                            @RequestParam(value = "duration",required = false) String duration,// 이상한 값이 넘어올 경우 duration 없이 조건
                                                                                            @RequestParam(value = "type", required = false) String type, //ExchangeType으로 변환 필요, ExchangeType 으로 변환 실패 시 null로 변환
                                                                                            @RequestParam(value = "badge",required = false) String requiredBadge, // Boolean 값으로 넘어오지 않을 경우 null로 변환
                                                                                            @RequestParam(value = "status",required = false) String status, //ExchangePostStatus으로 변환 필요, ExchangePostStatus 으로 변환 실패시  으로 변환 실패 시 null로 변환
                                                                                            @RequestParam(value = "page",required = false,defaultValue = "1") String page,
                                                                                            @RequestParam(value = "size",required = false,defaultValue = "15") String size){
        return CommonResponse.success(exchangePostService.getExchangePostList(categories,talents,order,duration,type,requiredBadge,status,page,size));
    }

    @PostMapping("/posts/exchanges")
    public ResponseEntity<CommonResponse<String>> writeExchangePost(@RequestBody @Valid ExchangePostReqDTO exchangePostReqDTO){
        return CommonResponse.success(exchangePostService.writeExchangePost(exchangePostReqDTO));
    }

}
