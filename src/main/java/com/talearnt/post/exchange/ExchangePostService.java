package com.talearnt.post.exchange;

import com.talearnt.admin.category.entity.TalentCategory;
import com.talearnt.admin.category.repository.TalentCategoryRepository;
import com.talearnt.enums.common.ErrorCode;
import com.talearnt.post.exchange.entity.ExchangePost;
import com.talearnt.post.exchange.entity.GiveTalent;
import com.talearnt.post.exchange.entity.ReceiveTalent;
import com.talearnt.post.exchange.repository.ExchangePostQueryRepository;
import com.talearnt.post.exchange.repository.ExchangePostRepository;
import com.talearnt.post.exchange.repository.GiveTalentRepository;
import com.talearnt.post.exchange.repository.ReceiveTalentRepository;
import com.talearnt.post.exchange.request.ExchangePostReqDTO;
import com.talearnt.user.talent.repository.MyTalentQueryRepository;
import com.talearnt.util.exception.CustomRuntimeException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Log4j2
@RequiredArgsConstructor
public class ExchangePostService {

    //JdbcTemplate
    private final JdbcTemplate jdbcTemplate;

    //Repositories
    private final ExchangePostQueryRepository exchangePostQueryRepository;
    private final MyTalentQueryRepository myTalentQueryRepository;
    private final ExchangePostRepository exchangePostRepository;
    private final ReceiveTalentRepository receiveTalentRepository;
    private final GiveTalentRepository giveTalentRepository;
    private final TalentCategoryRepository talentCategoryRepository;

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
    @Transactional
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

        //ExchangePost 저장
        ExchangePost savedPostEntity = exchangePostRepository.save(exchangePostEntity);

        //중복 키워드 코드 제거
        Set<Integer> codes = Stream.concat(exchangePostReqDTO.getGiveTalents().stream(),exchangePostReqDTO.getReceiveTalents().stream())
                .collect(Collectors.toSet());

        //제대로 된 키워드 코드로 넘어왔으면 값 가져와 캐싱
        Map<Integer, TalentCategory> categoryCache = talentCategoryRepository.findAllById(codes)
                .stream()
                .collect(Collectors.toMap(TalentCategory::getTalentCode, Function.identity()));

        //자식 테이블(GiveTalent,ReceiveTalent) 변환
        List<GiveTalent> giveTalentEntities = exchangePostReqDTO.getGiveTalents().stream().map(
                code->new GiveTalent(null,savedPostEntity,categoryCache.get(code))
        ).toList();
        List<ReceiveTalent> receiveTalentEntities = exchangePostReqDTO.getReceiveTalents().stream().map(
                code -> new ReceiveTalent(null,savedPostEntity,categoryCache.get(code))
        ).toList();

        //자식 테이블 저장
        //JDBC SQL 정의 여러 Method에 사용할 예정이면, 멤버 변수로 뺴기
        String giveTalentsSQL = "INSERT INTO give_talent (exchange_post_no, talent_code) VALUES (?, ?)";
        String receiveTalentsSQL =   "INSERT INTO receive_talent (exchange_post_no, talent_code) VALUES (?, ?)";

        // GiveTalent Bulk Insert 시작
        jdbcTemplate.batchUpdate(giveTalentsSQL,giveTalentEntities,5,
                (ps,entity)->{
                    ps.setLong(1,entity.getExchangePost().getExchangePostNo());
                    ps.setInt(2, entity.getTalentCode().getTalentCode());
                });

        jdbcTemplate.batchUpdate(receiveTalentsSQL,receiveTalentEntities,5,
                (ps,entity)->{
                    ps.setLong(1,entity.getExchangePost().getExchangePostNo());
                    ps.setInt(2, entity.getTalentCode().getTalentCode());
                });

        log.info("재능 교환 게시글 작성 끝");
        return "재능 교환 게시글 작성 완료";
    }

}
