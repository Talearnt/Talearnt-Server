package com.talearnt.post.exchange;

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

    public ResponseEntity<CommonResponse<List<ExchangePostListResDTO>>> getExchangePostList(){
        return null;
    }

    @PostMapping("/posts/exchange")
    public ResponseEntity<CommonResponse<String>> writeExchangePost(@RequestBody @Valid ExchangePostReqDTO exchangePostReqDTO){
        return CommonResponse.success(exchangePostService.writeExchangePost(exchangePostReqDTO));
    }

}
