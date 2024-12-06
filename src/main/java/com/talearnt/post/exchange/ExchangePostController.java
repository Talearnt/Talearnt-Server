package com.talearnt.post.exchange;

import com.talearnt.user.talent.MyTalentService;
import com.talearnt.user.talent.response.MyTalentsResDTO;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.version.RestControllerV1;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Tag(name = "Post-Exchange")
@RestControllerV1
@Log4j2
@RequiredArgsConstructor
public class ExchangePostController implements ExchangePostApi{

    private final MyTalentService myTalentService;

    @GetMapping("/post/exchange/provide-talents")
    public ResponseEntity<CommonResponse<List<MyTalentsResDTO>>> getWantGiveMyTalentsForPost(Authentication auth){
        return CommonResponse.success(myTalentService.getMyGiveTalents(auth));
    }

}
