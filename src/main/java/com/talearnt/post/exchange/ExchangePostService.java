package com.talearnt.post.exchange;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.post.exchange.entity.ExchangePost;
import com.talearnt.post.exchange.repository.ExchangePostQueryRepository;
import com.talearnt.post.exchange.request.ExchangePostReqDTO;
import com.talearnt.user.talent.repository.MyTalentQueryRepository;
import com.talearnt.util.exception.CustomRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class ExchangePostService {

    //Repositories
    private final ExchangePostQueryRepository exchangePostQueryRepository;
    private final MyTalentQueryRepository myTalentQueryRepository;

    /** 재능 교환 게시글 작성 <br>
     * 조건<br>
     * - 로그인이 되어 있는가? (Controller 에서 확인)<br>
     * - Valid(Controller 에서 확인)<br>
     * - GiveTalents 가 나의 재능의 주고 싶은 재능에 있는 키워드인가?<br>
     * - Give,Receive Talents 가 제대로된 키워드 코드로 넘어 왔는가?<br>
     *
     * 개선점<br>
     * - S3 도입 후 어떻게 저장되는가 확인
     * */
    public String writeExchangePost(ExchangePostReqDTO exchangePostReqDTO){
        log.info("재능 교환 게시글 작성 시작 : {}",exchangePostReqDTO);

        //주고 싶으 나의 재능 가져오기
        List<Integer> talentCodes = exchangePostQueryRepository.getWantGiveMyTalents(exchangePostReqDTO.getUserInfo().getUserNo());

        //GiveTalents 가 나의 재능의 주고 싶은 재능에 있는 키워드인가?
        talentCodes
                .stream()
                .filter(code-> !exchangePostReqDTO.getGiveTalents().contains(code))
                .findAny().ifPresent(code -> {
                    log.error("재능 교환 게시글 작성 실패 - 나의 주고 싶은 재능에 없는 코드가 들어옴 : {}", ErrorCode.POST_GIVE_MY_TALENT_NOT_FOUND);
                    throw new CustomRuntimeException(ErrorCode.POST_GIVE_MY_TALENT_NOT_FOUND);
                });
        //Give,Receive Talents 가 제대로된 키워드 코드로 넘어 왔는가?
        if(myTalentQueryRepository.validateIsCategory(exchangePostReqDTO.getGiveTalents())
                && myTalentQueryRepository.validateIsCategory(exchangePostReqDTO.getReceiveTalents())) {
            log.error("재능 교환 게시글 작성 실패 - 존재하지 않은 키워드 : {}", ErrorCode.KEYWORD_CATEGORY_NOT_FOUND);
            throw new CustomRuntimeException(ErrorCode.KEYWORD_CATEGORY_NOT_FOUND);
        }

        //ExchangePost Entity로 변환
        ExchangePost exchangePostEntity = ExchangePostMapper.INSTANCE.toExchangePostEntity(exchangePostReqDTO);



        log.info("재능 교환 게시글 작성 끝 : {}", exchangePostEntity);
        return null;
    }

}
