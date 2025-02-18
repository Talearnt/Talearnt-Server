package com.talearnt.post.exchange;

import com.querydsl.core.Tuple;
import com.talearnt.admin.category.entity.TalentCategory;
import com.talearnt.admin.category.repository.TalentCategoryRepository;
import com.talearnt.chat.ChatRoomMapper;
import com.talearnt.chat.entity.ChatRoom;
import com.talearnt.chat.repository.ChatRoomRepository;
import com.talearnt.enums.chat.RoomMode;
import com.talearnt.enums.common.ErrorCode;
import com.talearnt.enums.post.PostType;
import com.talearnt.post.exchange.entity.ExchangePost;
import com.talearnt.post.exchange.entity.GiveTalent;
import com.talearnt.post.exchange.entity.ReceiveTalent;
import com.talearnt.post.exchange.repository.ExchangePostQueryRepository;
import com.talearnt.post.exchange.repository.ExchangePostRepository;
import com.talearnt.post.exchange.repository.GiveTalentRepository;
import com.talearnt.post.exchange.repository.ReceiveTalentRepository;
import com.talearnt.post.exchange.request.ExchangePostReqDTO;
import com.talearnt.post.exchange.request.ExchangeSearchConditionDTO;
import com.talearnt.post.exchange.response.ExchangePostListResDTO;
import com.talearnt.post.exchange.response.ExchangePostDetailResDTO;
import com.talearnt.s3.S3Service;
import com.talearnt.s3.entity.FileUpload;
import com.talearnt.s3.repository.FileUploadRepository;
import com.talearnt.user.talent.repository.MyTalentQueryRepository;
import com.talearnt.util.common.PageUtil;
import com.talearnt.util.common.PostUtil;
import com.talearnt.util.common.UserUtil;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.jwt.UserInfo;
import com.talearnt.util.response.PaginatedResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Log4j2
@RequiredArgsConstructor
public class ExchangePostService {

    //JdbcTemplate
    private final JdbcTemplate jdbcTemplate;
    private final S3Service s3Service;

    //Repositories
    private final ExchangePostQueryRepository exchangePostQueryRepository;
    private final MyTalentQueryRepository myTalentQueryRepository;
    private final ExchangePostRepository exchangePostRepository;
    private final TalentCategoryRepository talentCategoryRepository;
    private final FileUploadRepository fileUploadRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final GiveTalentRepository giveTalentRepository;
    private final ReceiveTalentRepository receiveTalentRepository;

    /** 재능 교환 게시글 작성 <br>
     * 조건<br>
     * - 로그인이 되어 있는가? (Controller 에서 확인)<br>
     * - Valid(Controller 에서 확인)<br>
     * - GiveTalents 가 나의 재능의 주고 싶은 재능에 있는 키워드인가?<br>
     * - Give,Receive Talents 가 제대로된 키워드 코드로 넘어 왔는가?<br>
     *
     * 게시글이 작성되며 채팅방이 생성 되도록 변경함
     * */
    @Transactional
    public String writeExchangePost(ExchangePostReqDTO exchangePostReqDTO){
        log.info("재능 교환 게시글 작성 시작 : {}", exchangePostReqDTO);

        //주고 싶으 나의 재능 가져오기
        List<Integer> talentCodes = exchangePostQueryRepository.getWantGiveMyTalents(exchangePostReqDTO.getUserInfo().getUserNo());

        //GiveTalents 가 나의 재능의 주고 싶은 재능에 있는 키워드인가?
        List<Integer> invalidCodes = exchangePostReqDTO.getGiveTalents()
                .stream()
                .filter(code -> !talentCodes.contains(code)) // talentCodes에 없는 값만 필터링
                .toList();

        if (!invalidCodes.isEmpty()) {
            log.error("재능 교환 게시글 작성 실패 - 유효하지 않은 재능 코드 목록: {}, 에러 코드: {}", invalidCodes, ErrorCode.POST_GIVE_MY_TALENT_NOT_FOUND);
            throw new CustomRuntimeException(ErrorCode.POST_GIVE_MY_TALENT_NOT_FOUND);
        }
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

        //주고 싶은, 받고 싶은 키워드 합치기
        Set<Integer> codes = Stream.concat(exchangePostReqDTO.getGiveTalents().stream(), exchangePostReqDTO.getReceiveTalents().stream())
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

        //이미지를 업로드 했을 경우 DB에 저장
        if (exchangePostReqDTO.getImageUrls() != null && !exchangePostReqDTO.getImageUrls().isEmpty()){
            // 파일 업로드 경로 저장
            List<FileUpload> fileUploads = exchangePostReqDTO.getImageUrls().stream().map(
                    url-> new FileUpload(null,savedPostEntity.getExchangePostNo(), exchangePostReqDTO.getUserInfo().getUserNo(), PostType.EXCHANGE,url,null)
            ).toList();

            // 파일 업로드 경로 모두 저장
            fileUploadRepository.saveAll(fileUploads);
        }

        //채팅방 개설 전 Entity 설정
        ChatRoom chatRoomEntity = ChatRoomMapper.INSTANCE.toEntity(savedPostEntity, exchangePostReqDTO.getUserInfo(), RoomMode.PUBLIC);

        //채팅방 저장
        chatRoomRepository.save(chatRoomEntity);

        log.info("재능 교환 게시글 작성 끝");
        return "재능 교환 게시글 작성 완료";
    }



    /**재능 교환 게시글 목록 불러오기 <br>
     * 필터 조건 ( 커뮤니티에서도 사용할 것은 PostUtil 에서 Validate 정의, 재능 교환에서만 사용하는 것은 QueryDSL 정의) - 검증 완료<br>
     * - 대분류 : Integer 로 변환 필요 ( length 가 0일 경우 null ) - 검증 완료<br>
     * - 재능 분류 : Integer 로 변환 필요 ( length 가 0일 경우 null ) - 검증 완료<br>
     * - 정렬 기준 : 기본 recent, (recent, popular 가 아니라면 recent 로 변경) (커뮤니티 공통) - 검증 완료<br>
     * - 기간 : 이상한 값(Regex 에 맞지 않는)이 넘어왔을 경우에는 null로 변경 - 검증 완료<br>
     * - 진행 방식 : ExchangeType 값이 아닐 경우 null - 검증 완료<br>
     * - 인증 뱃지 필수 여부 : Boolean 값이 아닐 경우 null - 검증 완료<br>
     * - 모집 상태 : ExchangePostStatus 값이 아닐 경우 null - 검증 완료<br>
     * - 페이지 번호 : Integer 가 아닐 경우 기본 값 1 (커뮤니티 공통)<br>
     * */
    public PaginatedResponse<List<ExchangePostListResDTO>> getExchangePostList(List<String> giveTalents, List<String> receiveTalents, String order, String duration, String type, String requiredBadge, String status, String page, String size, String search, Authentication auth){
        log.info("재능 교환 게시글 목록 불러오기 시작");

        //유저가 로그인 했는 지 확인, 안했을 경우 찜 게시글 표시 False
        Long currentUserNo = getCurrentUserNo(auth);

        //DTO로 변환 하면서 값 유효한 값으로 생성자에서 변경
        ExchangeSearchConditionDTO searchCondition = ExchangeSearchConditionDTO.builder()
                .search(search)
                .giveTalents(giveTalents)
                .receiveTalents(receiveTalents)
                .order(order)
                .duration(duration)
                .type(type)
                .requiredBadge(requiredBadge)
                .status(status)
                .page(page)
                .size(size)
                .build();

        //조회하는 Query 실행
        Page<ExchangePostListResDTO> result = exchangePostQueryRepository.getFilteredExchangePostList(searchCondition, currentUserNo);

        log.info("재능 교환 게시글 목록 불러오기 끝");
        //데이터 넣고, Pagination으로 변환 -> 데이터와 Page 분리하여 보내기
        return new PaginatedResponse<>(result.getContent(),PageUtil.separatePaginationFromEntity(result));
    }


    /** 재능교환 게시글 상세보기 <br>
     * 조건 )<br>
     * - 로그인이 되어 있는가? (찜 여부)<br>
     * - 게시글이 존재하는가?<br>
     * - 조회수 상승 필요
     * */
    public ExchangePostDetailResDTO getExchangePostDetail(Long postNo, Authentication auth){
        log.info("재능 교환 게시글 상세 보기 시작");

        //유저가 로그인 했는 지 확인, 안했을 경우 찜 게시글 표시 False
        Long currentUserNo = getCurrentUserNo(auth);

        ExchangePostDetailResDTO result = exchangePostQueryRepository.getPostDetail(postNo,currentUserNo)
                .orElseThrow(()->{
                    log.error("재능 교환 게시글 상세보기 실패 - 해당 게시글 없음 : {}",ErrorCode.POST_NOT_FOUND);
                    return new CustomRuntimeException(ErrorCode.POST_NOT_FOUND);
                });

        log.info("재능 교환 게시글 상세 보기 끝");
        return result;
    }


    /** 재능 교환 게시글 수정
     * 조건 )
     * - 로그인이 되어 있는가?
     * - 나의 게시글이 맞는가?
     * - 주고 싶은 재능이 나의 재능 과거 이력과 현재 이력에 있는가?
     * - 올바른 값이 넘어 왔는가? (Valid)
     * - 새로운 이미지가 있는가? DB 업로드
     * - 사라진 이미지가 있는가? DB 삭제 및 S3 삭제
     * - */
    @Transactional
    public String updateExchangePost(Long postNo, ExchangePostReqDTO exchangePostReqDTO){
        log.info("재능 교환 게시글 수정 시작 : {}",postNo);

        //나의 게시글이 맞는가?
        if(!exchangePostQueryRepository.isMyExchangePost(postNo, exchangePostReqDTO.getUserInfo().getUserNo())){
            log.error("재능 교환 게시글 수정 실패 - 내 게시글이 아님 : {} - {}",postNo,ErrorCode.POST_ACCESS_DENIED);
            throw new CustomRuntimeException(ErrorCode.POST_ACCESS_DENIED);
        }
        // 주고 싶은 재능이 나의 재능 이력에 존재하는가?
        List<Integer> myTalents = exchangePostQueryRepository.getPastMyTalents(exchangePostReqDTO.getUserInfo().getUserNo());
        if (myTalents.isEmpty()){
            log.error("재능 교환 게시글 수정 실패 - 나의 재능 이력이 존재하지 않음 : {} - {}",exchangePostReqDTO.getUserInfo().getUserNo(), ErrorCode.MY_TALENT_KEYWORD_NOT_REGISTERED);
            throw new CustomRuntimeException(ErrorCode.MY_TALENT_KEYWORD_NOT_REGISTERED);
        }

        List<Integer> notContainsTalentCode = exchangePostReqDTO.getGiveTalents().stream()
                .filter(talentCode -> !myTalents.contains(talentCode))
                .toList();

        if (!notContainsTalentCode.isEmpty()){
            log.error("재능 교환 게시글 수정 실패 - 나의 재능 이력에 존재하지 않는 코드 존재 : {} - {}", exchangePostReqDTO.getUserInfo().getUserNo(), ErrorCode.POST_GIVE_MY_TALENT_NOT_FOUND);
            throw new CustomRuntimeException(ErrorCode.POST_GIVE_MY_TALENT_NOT_FOUND);
        }

        //게시글 업데이트
        long updatedPostCount = exchangePostQueryRepository.updateExchangePost(postNo,exchangePostReqDTO.getTitle(),exchangePostReqDTO.getContent(),exchangePostReqDTO.getExchangeType(),exchangePostReqDTO.isRequiredBadge(),exchangePostReqDTO.getDuration());
        if(updatedPostCount == 0 || updatedPostCount > 1){
            log.error("재능 교환 게시글 수정 실패 - 수정된 게시글이 0개 또는 여러 개입니다. : {} - {}",postNo,ErrorCode.POST_FAILED_UPDATE);
            throw new CustomRuntimeException(ErrorCode.POST_FAILED_UPDATE);
        }

        //주고 싶은, 받고 싶은 코드 조회 Keys = giveTalentCodes, receiveTalentCodes
        Map<String, List<Tuple>> codes = exchangePostQueryRepository.getGiveAndReceiveTalentCodesByPostNo(postNo);
        updateGiveAndReceiveTalents(postNo, codes, exchangePostReqDTO.getGiveTalents(), exchangePostReqDTO.getReceiveTalents());

        //해당 게시글의 업로드된 이미지 가져오기


        log.info("재능 교환 게시글 수정 끝 : {}",postNo);
        return "재능 교환 게시글 수정이 성공적으로 이루어졌습니다.";
    }




    //주고 싶은, 받고 싶은 재능 업데이트
    private void updateGiveAndReceiveTalents(Long postNo, Map<String,List<Tuple>> codes,List<Integer> willUpdateGiveTalentCodes, List<Integer> willUpdateReceiveTalentCodes){
        //게시글 번호로 생성
        ExchangePost changedExchangePost = new ExchangePost(postNo);

        //Tuple -> Map으로 변경
        Map<Long, Integer> giveTalentMap = PostUtil.getTalentMap("giveTalentCodes", codes);

        //유지할 값 추출 - 주고 싶은 재능
        Map<Long,Integer> sameGiveCodes = PostUtil.getSameCodes(giveTalentMap,willUpdateGiveTalentCodes);

        //변경할 값 추출 - 주고 싶은 재능
        Map<Long, Integer> updateGiveTalentCodes =PostUtil.getUpdateTalentCodes(giveTalentMap, willUpdateGiveTalentCodes, sameGiveCodes);
        //변경할 값 저장
        exchangePostQueryRepository.updateGiveTalents(updateGiveTalentCodes);

        //추가할 값 추출 - 주고 싶은 재능
        List<Integer> addGiveTalentCodes = PostUtil.getAddTalentCodes(willUpdateGiveTalentCodes,giveTalentMap,updateGiveTalentCodes);
        //추가할 값이 있으면 저장
        if(!addGiveTalentCodes.isEmpty()) {
            List<GiveTalent> addGiveTalents = addGiveTalentCodes.stream()
                    .map(talentCode -> new GiveTalent(null, changedExchangePost, new TalentCategory(talentCode)))
                    .toList();

            String giveTalentsSQL = "INSERT INTO give_talent (exchange_post_no, talent_code) VALUES (?, ?)";
            // GiveTalent Bulk Insert 시작
            jdbcTemplate.batchUpdate(giveTalentsSQL,addGiveTalents,5,
                    (ps,entity)->{
                        ps.setLong(1,entity.getExchangePost().getExchangePostNo());
                        ps.setInt(2, entity.getTalentCode().getTalentCode());
                    });
        }

        //삭제할 값 추출 - 주고 싶은 재능
        List<Long> deleteGiveNos = PostUtil.getDeleteIds(giveTalentMap, sameGiveCodes, updateGiveTalentCodes);
        //삭제 실행
        exchangePostQueryRepository.deleteGiveTalents(deleteGiveNos);

        //Tuple -> Map으로 변경 - 받고 싶은 재능
        Map<Long, Integer> receiveTalentMap = PostUtil.getTalentMap("receiveTalentCodes", codes);

        //유지할 값 추출 - 받고 싶은 재능
        Map<Long,Integer> sameReceiveCodes = PostUtil.getSameCodes(receiveTalentMap,willUpdateReceiveTalentCodes);

        //변경할 값 추출 - 받고 싶은 재능
        Map<Long, Integer> updateReceiveTalentCodes =PostUtil.getUpdateTalentCodes(receiveTalentMap, willUpdateReceiveTalentCodes, sameReceiveCodes);
        //변경할 값 저장
        exchangePostQueryRepository.updateReceiveTalents(updateReceiveTalentCodes);

        //추가할 값 추출 - 받고 싶은 재능
        List<Integer> addReceiveTalentCodes = PostUtil.getAddTalentCodes(willUpdateReceiveTalentCodes,receiveTalentMap,updateReceiveTalentCodes);
        //추가할 값이 있으면 저장
        if(!addReceiveTalentCodes.isEmpty()) {
            List<ReceiveTalent> addReceiveTalents = addReceiveTalentCodes.stream()
                    .map(talentCode -> new ReceiveTalent(null, changedExchangePost, new TalentCategory(talentCode)))
                    .toList();

            String receiveTalentsSQL =   "INSERT INTO receive_talent (exchange_post_no, talent_code) VALUES (?, ?)";
            // GiveTalent Bulk Insert 시작
            jdbcTemplate.batchUpdate(receiveTalentsSQL,addReceiveTalents,5,
                    (ps,entity)->{
                        ps.setLong(1,entity.getExchangePost().getExchangePostNo());
                        ps.setInt(2, entity.getTalentCode().getTalentCode());
                    });
        }

        //삭제할 값 추출 - 주고 싶은 재능
        List<Long> deleteReceiveNos = PostUtil.getDeleteIds(receiveTalentMap, sameReceiveCodes, updateReceiveTalentCodes);
        //삭제 실행
        exchangePostQueryRepository.deleteReceiveTalents(deleteReceiveNos);

    }


    /**재능 교환 게시글 삭제
     * 조건 )
     * - 로그인이 되어 있는가?
     * - 나의 게시글이 맞는가?
     * */
    @Transactional
    public String deleteExchangePost(Long postNo, Authentication auth){
        log.info("재능 교환 게시글 삭제 시작");

        //로그인 여부 확인
        UserInfo userInfo = UserUtil.validateAuthentication("재능교환 게시글 삭제", auth);

        //나의 게시글이 맞는가?
        if(!exchangePostQueryRepository.isMyExchangePost(postNo, userInfo.getUserNo())){
            log.error("재능 교환 게시글 삭제 실패 - 내 게시글이 아님 : {} - {}",postNo,ErrorCode.POST_ACCESS_DENIED);
            throw new CustomRuntimeException(ErrorCode.POST_ACCESS_DENIED);
        }

        //재능 교환 게시글 소프트 삭제
        long deletedPostCount = exchangePostQueryRepository.deleteExchangePostByPostNo(postNo);
        if (deletedPostCount != 1){
            log.error("재능 교환 게시글 삭제 실패 - 삭제된 게시글이 0개 또는 여러 개입니다 : {}",ErrorCode.POST_FAILED_DELETE);
            throw new CustomRuntimeException(ErrorCode.POST_FAILED_DELETE);
        }

        log.info("재능 교환 게시글 삭제 끝");
        return "재능 교환 게시글이 성공적으로 삭제 되었습니다.";
    }

    //찜 게시글, 로그인 하지 않았더라도 여부 알기용.
    private Long getCurrentUserNo(Authentication auth){
        Long currentUserNo = 0L;
        if (auth != null){
            UserInfo userInfo = UserUtil.validateAuthentication("재능 교환 게시글 상세 보기",auth);
            currentUserNo = userInfo.getUserNo();
        }
        return currentUserNo;
    }


}
