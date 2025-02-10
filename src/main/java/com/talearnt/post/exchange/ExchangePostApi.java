package com.talearnt.post.exchange;

import com.talearnt.post.exchange.request.ExchangePostReqDTO;
import com.talearnt.post.exchange.response.ExchangePostDetailResDTO;
import com.talearnt.post.exchange.response.ExchangePostListResDTO;
import com.talearnt.user.talent.response.MyTalentsResDTO;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.response.PaginatedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ExchangePostApi {


    @Operation(summary = "게시물 작성 페이지 - 주고 싶은 재능 불러 오기",
            description = "<h2>내용</h2>" +
                    "<p>재능 게시글 등록 페이지에서 호출할 내용입니다.<p>" +
                    "<p>주고 싶은 재능에 들어갈 내용입니다.</p>" +
                    "<hr>" +
                    "<p>재능 키워드 중 활성화 되어 있는 것들만 가져옵니다.</p>" +
                    "<p>과거 활성화된 재능 키워드를 등록했으나 현재는 사용하지 않는 재능 키워드가 나의 재능에 등록되어 있을 경우</p>" +
                    "<p>비활성화된 키워드를 제외하고 가져옵니다.</p>" +
                    "<p>List 길이가 0일 수도 있습니다.</p>" +
                    "<h2>Response</h2>" +
                    "<ul>" +
                    "<li><strong>myTalentNo :</strong> 나의 재능 번호</li>" +
                    "<li><strong>talentName :</strong> 재능 이름</li>" +
                    "</ul>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", ref = "EXPIRED_TOKEN"),
            @ApiResponse(responseCode = "403", ref = "ACCESS_DENIED")
    })
    public ResponseEntity<CommonResponse<List<MyTalentsResDTO>>> getWantGiveMyTalentsForPost(Authentication auth);


    @Operation(summary = "게시글 등록",
            description = "<h2>내용</h2>" +
                    "<p>재능 게시글 등록입니다. ( S3 미구현 - 추후 구현 )<p>" +
                    "<hr/>"+
                    "<h2>Request Body</h2>" +
                    "<ul>" +
                    "<li><strong>title :</strong> 2자 이상, 50자 이하</li>" +
                    "<li><strong>content :</strong> 20자 이상</li>" +
                    "<li><strong>giveTalents :</strong> 1개 이상, 5개 이하</li>" +
                    "<li><strong>receiveTalents :</strong> 1개 이상, 5개 이하</li>" +
                    "<li><strong>exchangeType :</strong> 진행 방식(온라인,오프라인,온/오프라인)</li>" +
                    "<li><strong>requiredBadge :</strong> 인증 뱃지 필수 여부 - 기본 false</li>" +
                    "<li><strong>duration :</strong> 진행 기간(기간 미정,1개월,2개월,3개월,3개월 이상)</li>" +
                    "<li><strong>imageUrls :</strong> S3에 저장된 이미지 경로 목록 - Presigned URL 에서 ?의 뒷 부분은 제거하고 보내주세요.</li>" +
                    "</ul>"+
                    "<hr/>"+
                    "<h2>선행 필수 내용</h2>"+
                    "<p>로그인을 하지 않으면 오류가 발생합니다.</p>"+
                    "<p>키워드 등록을 하지 않으면 오류가 발생합니다. (나의 주고 싶은 재능 등록 필요)</p>"+
                    "<p>아래는 Enum Class 및 Regex 규칙이 적용되었습니다.</p>"+
                    "<p>ExchangeType : 온라인,오프라인,온/오프라인 이렇게 보내주셔야 합니다.</p>"+
                    "<p>Duration : 기간 미정,1개월,2개월,3개월,3개월 이상 이렇게 보내주셔야 합니다.</p>" +
                    "<hr/>" +
                    "<h2>업로드할 경우 FE에서 해야 할 업무</h2>" +
                    "<ol>" +
                        "<li>파일 이름 인코딩</li>" +
                        "<li>이미지 업로드 성공 시 urls에 경로 담기</li>" +
                        "<li>5MB Byte가 넘을 경우 압축 OR 압축 불가 시 등록 불가</li>" +
                    "</ol>" +
                    "<p><strong>웹에서는 CTRL+Z를 누를 경우 이전 작업</strong>으로 이동하는데 그 경우 Delete에 있는 값이 URLS로 이동하는 지 확인 작업 필요!</p>"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401-1", ref = "EXPIRED_TOKEN"),
            @ApiResponse(responseCode = "401-2", ref = "INVALID_TOKEN"),
            @ApiResponse(responseCode = "404-1", ref = "POST_GIVE_MY_TALENT_NOT_FOUND"),
            @ApiResponse(responseCode = "404-2", ref = "KEYWORD_CATEGORY_NOT_FOUND"),
            @ApiResponse(responseCode = "400-1", ref = "POST_TITLE_LENGTH_MISSING"),
            @ApiResponse(responseCode = "400-2", ref = "POST_CONTENT_MIN_LENGTH"),
            @ApiResponse(responseCode = "400-3", ref = "POST_KEYWORD_LENGTH_MISSING"),
            @ApiResponse(responseCode = "400-4", ref = "POST_KEYWORD_LENGTH_OVER"),
            @ApiResponse(responseCode = "400-5", ref = "POST_BAD_REQUEST"),
            @ApiResponse(responseCode = "400-6", ref = "POST_DURATION_MISSING"),
    })
    public ResponseEntity<CommonResponse<String>> writeExchangePost(@RequestBody @Valid ExchangePostReqDTO exchangePostReqDTO);


    @Operation(summary = "게시글 상세보기"
            , description = "<h2>내용</h2>" +
            "<p>재능 교환 게시글 상세보기 입니다.</p>" +
            "<p>조회수 증가에 대한 조건은 없습니다. 같은 IP, 같은 ID가 봐도 조회수는 증가합니다.</p>" +
            "<p>give, receive는 현재 재능 이름만 넘어가고 있습니다. <strong>필요하면 Code도 같이 넘어가도록 변경</strong>하겠습니다.</p>" +
            "<p>채팅방에 연결 요청할 수 있는 chatRoomNo가 있습니다.</p>" +
            "<p>채팅방 구현 시에 필요해 미리 넣어뒀습니다.</p>" +
            "<hr/>" +
            "<h2>Response</h2>" +
            "<ul>" +
                "<li>userNo : 유저 번호 - 내 게시글 판단하기 위함</li>" +
                "<li>nickname : 유저 닉네임</li>" +
                "<li>profileImg : 유저 프로필 사진 경로</li>" +
                "<li>authority : 유저 권한 - MVP2 인증 유저용</li>" +
                "<li>exchangePostNo : 게시글 번호</li>" +
                "<li>giveTalents : 주고 싶은 재능 목록 (이름)</li>" +
                "<li>receiveTalents : 받고 싶은 재능 목록 (이름)</li>" +
                "<li>exchangeType : 온라인,오프라인,온_오프라인</li>" +
                "<li>createdAt : 게시글 등록일시</li>" +
                "<li>duration : 진행 기간 - 1개월,2개월 등등</li>" +
                "<li>requiredBadge : 인증뱃지 필요 여부</li>" +
                "<li>isFavorite : 찜 게시글 여부</li>" +
                "<li>title : 게시글 제목</li>" +
                "<li>content : 게시글 내용</li>" +
                "<li>images : 이미지 경로 목록</li>" +
                "<li>count : 조회수</li>" +
                "<li>favoriteCount : 게시글의 찜 개수</li>" +
                "<li>openedChatRoomCount : 해당 게시글에 열린 채팅방 개수</li>" +
                "<li>chatRoomNo : 채팅방에 연결할 번호</li>" +
            "</ul>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200")
    })
    public ResponseEntity<CommonResponse<ExchangePostDetailResDTO>> getExchangePostDetail(@PathVariable Long postNo, Authentication auth);



    @Operation(summary = "재능 교환 게시글 목록"
            , description = "<h2>내용</h2>" +
            "<p>giveTalents, receiveTalents는 List<String>으로 받고 있습니다.</p>"+
            "<p>잘못된 값이 들어와도 서버에서 오류 발생을 방지하기 위하여 Query Parameter Type이 String으로 구성되어 있습니다.</p>"+
            "<p>유효한 값이 아닐 경우, 그 값은 제외하고 검색을 시작합니다</p>"+
            "<p>ex) giveTalents=1001,일공공이 -> 1001만 필터 조건으로 걸림</p>"+
            "<h3>Size</h3>"+
            "<p>재능 교환 게시글 목록을 15개(기본) 반환합니다.</p>"+
            "<p>Size로 원하는 갯수만큼 반환할 수 있습니다.</p>"+
            "<p>Content의 내용에 HTML 태그 제거 및 100자까지 뽑아서 반환합니다.</p>"+
            "<h3>제목 검색 (MVP 1차 미사용)</h3>"+
            "<p>제목 검색은 Ngram parser 2 Length를 사용하고 있습니다.</p>"+
            "<p>명확한 검색 결과를 얻기 위해서는 2글자 이상을 검색해야 값이 나옵니다.</p>"+
            "<p>Ngram Parser의 문제점은 띄어쓰기를 포함한 4글자는 검색 결과가 제대로 나오지 않습니다</p>"+
            "<p>ex)네 번째 -> 검색X </p>"+
            "<p>네 번째로 검색시에 번째에 포함되는 게시글을 불러올 수도 있으나 검색에 대한 신뢰도가 하락하는 문제가 있습니다.</p>"+
            "<p>ElasticSearch를 도입하면 검색 결과에 신뢰를 더할 수 있습니다만, 검색이 들어가는 MVP에서 도입을 검토 해야 할 것 같습니다.</p>"+
            "<hr>" +
            "<h2>Response</h2>" +
            "<ul>" +
                "<li>profileImg : 작성자의 프로필 이미지 경로</li>" +
                "<li>nickname : 작성자의 닉네임</li>" +
                "<li>authority : 작성자의 권한 (인증 유저)</li>" +
                "<li>exchangePostNo : 게시글 번호</li>" +
                "<li>status : 게시글 모집 상태</li>" +
                "<li>exchangeType : 게시글 진행 방식</li>" +
                "<li>duration : 게시글 진행 기간</li>" +
                "<li>title : 게시글 제목</li>" +
                "<li>giveTalents : 게시글 주고 싶은 재능</li>" +
                "<li>receiveTalents : 게시글 받고 싶은 재능</li>" +
                "<li>createdAt : 게시글 작성일</li>" +
                "<li>openedChatRoomCount : 신청된 채팅방 갯수</li>" +
                "<li>favoriteCount : 게시글 찜 갯수</li>" +
                "<li>isFavorite : 게시글 찜 여부</li>" +
                "<br>" +
                "<li>hasNext : 다음 버튼 여부</li>" +
                "<li>hasPrevious : 이전 버튼 여부</li>" +
                "<li>totalPages : 총 페이지 수 </li>" +
                "<li>currentPage : 현재 페이지</li>" +
            "</ul>"
    )
    public ResponseEntity<PaginatedResponse<List<ExchangePostListResDTO>>> getExchangePostList(@RequestParam(value = "giveTalents",required = false,defaultValue = "") @Schema(description = "주고 싶은 재능 코드 List") List<String> giveTalents,//Integer로 변환 필요
                                                                                               @RequestParam(value = "receiveTalents",required = false,defaultValue = "") @Schema(description = "받고 싶은 재능 코드 List") List<String> receiveTalents,//Integer로 변환 필요
                                                                                               @RequestParam(value = "order", required = false,defaultValue = "recent") @Schema(description = "정렬 - recent : 최신순, popular : 인기순") String order,//recent,popular 로 변환 필요
                                                                                               @RequestParam(value = "duration",required = false) @Schema(description = "진행 기간 - 기간 미정, 1개월,2개월,3개월, 3개월 이상 만 가능") String duration,// 이상한 값이 넘어올 경우 duration 없이 조건
                                                                                               @RequestParam(value = "type", required = false) @Schema(description = "진행 방식 - 온라인, 오프라인, 온_오프라인 만 가능") String type, //ExchangeType으로 변환 필요, ExchangeType 으로 변환 실패 시 null로 변환
                                                                                               @RequestParam(value = "badge",required = false) @Schema(description = "인증 뱃지 필요 여부 - true, false 만 가능") String requiredBadge, // Boolean 값으로 넘어오지 않을 경우 null로 변환
                                                                                               @RequestParam(value = "status",required = false) @Schema(description = "모집 상태 - 모집중, 모집_완료 만 가능") String status, //ExchangePostStatus으로 변환 필요, ExchangePostStatus 으로 변환 실패시  으로 변환 실패 시 null로 변환
                                                                                               @RequestParam(value = "page",required = false,defaultValue = "1") @Schema(description = "기본 1") String page,
                                                                                               @RequestParam(value = "size",required = false,defaultValue = "15") @Schema(description = "입력 X 기본 15개 반환, 필요시 50개 이하 호출 가능, 그 이상 불가능") String size,
                                                                                               @RequestParam(value = "search",required = false) @Schema(description = "Ngram Parse 사용중, 기본 2글자부터 검색 시 제대로 반환") String search,
                                                                                               Authentication auth);
    @Operation(summary = "재능 교환 게시글 수정",
            description = "<h2>내용</h2>" +
                    "<p>게시글 작성 부분과 똑같은 Request 형태를 가지고 있습니다.</p>" +
                    "<p>다만, imageUrls 부분을 신경써서 보내주셔야 합니다.</p>" +
                    "<p>기존 이미지 + 추가한 이미지 를 imagesUrls에 담아서 보내주시고, 삭제했다면 imageUrls에서 목록에서 제거해서 보내주셔야 합니다.</p>" +
                    "<p>서버 쪽에서 재능 교환 게시글 번호에 해당하는 이미지들을 가져온 후, DB에 있지만 imageUrls 없는 값들은 DB 삭제 후 서버에서 S3로 삭제 요청을 날립니다.</p>" +
                    "<p>DB에 없고 imageUrls에 있는 값은 DB에 추가합니다.</p>" +
                    "<p>이 부분은 BE만으로 테스트 하기 어려워 아직 테스트 하지 않았습니다.</p>" +
                    "<p>FE 분들의 게시글 작성 부분이 끝나 이미지 업로드까지 성공적으로 마친 경우 게시글 수정에서 이미지 부분을 처리하도록 하겠습니다.</p>" +
                    "<p>이미지를 제외한 값은 수정 가능합니다.</p>" +
                    "<p>Response가 수정된 게시글 내용으로 가져오시길 원하면 말씀주시면 그 값들로 변경해서 보내드리도록 하겠습니다.</p>" +
                    "<hr/>" +
                    "<h2>Request Body</h2>" +
                    "<ul>" +
                    "<li><strong>title :</strong> 2자 이상, 50자 이하</li>" +
                    "<li><strong>content :</strong> 20자 이상</li>" +
                    "<li><strong>giveTalents :</strong> 1개 이상, 5개 이하</li>" +
                    "<li><strong>receiveTalents :</strong> 1개 이상, 5개 이하</li>" +
                    "<li><strong>exchangeType :</strong> 진행 방식(온라인,오프라인,온/오프라인)</li>" +
                    "<li><strong>requiredBadge :</strong> 인증 뱃지 필수 여부 - 기본 false</li>" +
                    "<li><strong>duration :</strong> 진행 기간(기간 미정,1개월,2개월,3개월,3개월 이상)</li>" +
                    "<li><strong>imageUrls :</strong> S3에 저장된 이미지 경로 목록 - Presigned URL 에서 ?의 뒷 부분은 제거하고 보내주세요.</li>" +
                    "</ul>"+
                    "<hr/>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "403", ref = "POST_ACCESS_DENIED"),
            @ApiResponse(responseCode = "404-1", ref = "MY_TALENT_KEYWORD_NOT_REGISTERED"),
            @ApiResponse(responseCode = "404-2", ref = "POST_GIVE_MY_TALENT_NOT_FOUND"),
            @ApiResponse(responseCode = "400", ref = "POST_FAILED_UPDATE"),
    })
    public ResponseEntity<CommonResponse<String>> updateExchangePost(@PathVariable Long postNo, @RequestBody @Valid ExchangePostReqDTO exchangePostReqDTO);

}

