package com.talearnt.post.exchange;

import com.talearnt.enums.common.ClientPathType;
import com.talearnt.post.exchange.request.ExchangePostReqDTO;
import com.talearnt.post.exchange.request.ExchangePostStatusReqDTO;
import com.talearnt.post.exchange.response.ExchangePostDetailResDTO;
import com.talearnt.post.exchange.response.ExchangePostListResDTO;
import com.talearnt.user.talent.response.MyTalentsResDTO;
import com.talearnt.util.common.ClientPath;
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
                    "<p>재능 게시글 등록입니다.<p>" +
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
                    "</ul>" +
                    "<hr/>" +
                    "<h2>선행 필수 내용</h2>" +
                    "<p>로그인을 하지 않으면 오류가 발생합니다.</p>" +
                    "<p>키워드 등록을 하지 않으면 오류가 발생합니다. (나의 주고 싶은 재능 등록 필요)</p>" +
                    "<p>아래는 Enum Class 및 Regex 규칙이 적용되었습니다.</p>" +
                    "<p>ExchangeType : 온라인,오프라인,온/오프라인 이렇게 보내주셔야 합니다.</p>" +
                    "<p>Duration : 기간 미정,1개월,2개월,3개월,3개월 이상 이렇게 보내주셔야 합니다.</p>" +
                    "<hr/>" +
                    "<h2>업로드할 경우 FE에서 해야 할 업무</h2>" +
                    "<ol>" +
                    "<li>파일 이름 인코딩</li>" +
                    "<li>이미지 업로드 성공 시 urls에 경로 담기</li>" +
                    "<li>5MB Byte가 넘을 경우 압축 OR 압축 불가 시 등록 불가</li>" +
                    "</ol>" +
                    "<p><strong>웹에서는 CTRL+Z를 누를 경우 이전 작업</strong>으로 이동하는데 그 경우 Delete에 있는 값이 URLS로 이동하는 지 확인 작업 필요!</p>" +
                    "<hr>" +
                    "<h3>변경된 점</h3>" +
                    "<p>반환 값이 변경되었습니다. '성공적으로 재능 교환 게시글 등록하였습니다.' String 값이 넘어갔으나 이제는 작성된 내용을 보냅니다.</p>" +
                    "<hr>" +
                    "<h2>Response</h2>" +
                    "<ul>" +
                    "<li>등록한 게시글 번호</li>" +
                    "</ul>"
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
    public ResponseEntity<CommonResponse<Long>> writeExchangePost(@RequestBody @Valid ExchangePostReqDTO exchangePostReqDTO);


    @Operation(summary = "게시글 상세보기"
            , description = "<h2>내용</h2>" +
            "<p>재능 교환 게시글 상세보기 입니다.</p>" +
            "<p>조회수 증가에 대한 조건은 없습니다. 같은 IP, 같은 ID가 봐도 조회수는 증가합니다.</p>" +
            "<p>give, receive는 현재 재능 이름만 넘어가고 있습니다. <strong>필요하면 Code도 같이 넘어가도록 변경</strong>하겠습니다.</p>" +
            "<p>채팅방에 연결 요청할 수 있는 chatRoomNo가 있습니다.</p>" +
            "<p>채팅방 구현 시에 필요해 미리 넣어뒀습니다.</p>" +
            "<p>이미지 모아보기의 순서를 오름차순으로 정렬하여 순서에 맞게 보내줍니다.</p>" +
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
            "<li>exchangeType : 온라인,오프라인,온/오프라인</li>" +
            "<li>status : 모집중,모집 완료</li>" +
            "<li>createdAt : 게시글 등록 일시</li>" +
            "<li>updatedAt : 게시글 수정 일시</li>" +
            "<li>duration : 진행 기간 - 1개월,2개월 등등</li>" +
            "<li>requiredBadge : 인증뱃지 필요 여부</li>" +
            "<li>isFavorite : 찜 게시글 여부</li>" +
            "<li>title : 게시글 제목</li>" +
            "<li>content : 게시글 내용</li>" +
            "<li>imageUrls : 이미지 경로 목록</li>" +
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
            , description = "<h2>공통 참고 내용</h2>" +
            "<p>게시글 목록은 단 하나의 경우를 제외하고 값을 제대로 보내기 위하여 모든 값을 String 또는 List<String>으로 받고 있습니다.</p>" +
            "<p>path는 반드시 보내주셔야 합니다. 안보낼 경우 web으로 기본 설정되어 있습니다. (web|mobile)</p>" +
            "<h2><모바일 참고 내용></h2>" +
            "<h2>2페이지 이상 호출 시 반드시 필요한 값 - 모바일</h2>" +
            "<ul>" +
                "<li>lastNo : 마지막 게시글 번호</li>" +
            "</ul>" +
            "<p>lastNo는 다음 페이지에 존재하는 값을 불러오기 위한 번호입니다.</p>" +
            "<p><strong>lastNo가 있지만 Page 번호가 2 이상일 경우 중복/누락 데이터가 발생</strong>하여 UX/DX를 개선하기 위해 이례적으로 Exception을 발생시킵니다.</p>" +
            "<p><strong>Page 번호를 입력 안하시면 됩니다.</strong></p>" +
            "<hr>" +
            "<hr>" +
            "<h2><웹 참고 내용></h2>" +
            "<h2>2페이지 이상 호출 시 반드시 필요한 값 - 웹</h2>" +
            "<ul>" +
                "<li>page : 다음 페이지 번호</li>" +
            "</ul>" +
            "<p>page는 N번째 부터 Size 까지 뽑아오기 위해 존재합니다.</p>" +
            "<p><strong>lastNo가 존재할 경우 중복/누락 데이터가 발생</strong>하여 UX/DX를 개선하기 위해 이례적으로 Exception을 발생시킵니다.</p>" +
            "<hr>" +
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
                "<li>content : 게시글 내용 (태그를 제외한 100자 이내)</li>" +
                "<li>giveTalents : 게시글 주고 싶은 재능</li>" +
                "<li>receiveTalents : 게시글 받고 싶은 재능</li>" +
                "<li>createdAt : 게시글 작성일</li>" +
                "<li>count : 게시글 조회수</li>" +
                "<li>openedChatRoomCount : 신청된 채팅방 갯수</li>" +
                "<li>favoriteCount : 게시글 찜 갯수</li>" +
                "<li>isFavorite : 게시글 찜 여부</li>" +
            "</ul>" +
            "<hr>" +
            "<h2>Pagination 정보 - Mobile</h2>" +
            "<ul>" +
                "<li>hasNext - 다음 페이지 이동 가능 여부</li>" +
            "</ul>" +
            "<hr>" +
            "<h2>Pagination 정보 - Web</h2>" +
            "<ul>" +
                "<li>hasNext - 다음 페이지 이동 가능 여부</li>" +
                "<li>hasPrevious - 이전 페이지 이동 가능 여부</li>" +
                "<li>totalCount - 총 데이터 개수</li>" +
                "<li>totalPages - 총 페이지 개수</li>" +
                "<li>currentPage - 현재 페이지 번호</li>" +
                "<li>latestCreatedAt - 가장 최근 Data 작성일</li>" +
            "</ul>"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", ref = "POST_FAILED_CALL_LIST"),
    })
    public ResponseEntity<CommonResponse<PaginatedResponse<List<ExchangePostListResDTO>>>> getExchangePostList(@RequestParam(value = "giveTalents", required = false, defaultValue = "") @Schema(description = "주고 싶은 재능 코드 List") List<String> giveTalents,
                                                                                                               @RequestParam(value = "receiveTalents", required = false, defaultValue = "") @Schema(description = "받고 싶은 재능 코드 List") List<String> receiveTalents,
                                                                                                               @RequestParam(value = "order", required = false, defaultValue = "recent") @Schema(description = "정렬(기본) - recent : 최신순, popular : 인기순") String order,
                                                                                                               @RequestParam(value = "duration", required = false) @Schema(description = "진행 기간 - 기간 미정, 1개월,2개월,3개월, 3개월 이상 만 가능 - null일 경우 전체") String duration,
                                                                                                               @RequestParam(value = "type", required = false) @Schema(description = "진행 방식 - 온라인, 오프라인, 온/오프라인(온_오프라인) 만 가능 - null일 경우 전체") String type,
                                                                                                               @RequestParam(value = "badge", required = false) @Schema(description = "인증 뱃지 필요 여부 - true, false 만 가능 - null 일 경우 전체") String requiredBadge,
                                                                                                               @RequestParam(value = "status", required = false) @Schema(description = "모집 상태 - 모집중, 모집 완료(모집_완료) 만 가능 - null일 경우 전체") String status,
                                                                                                               @RequestParam(value = "page",required = false,defaultValue = "1") @Schema(description = "모바일 사용 X (모바일은 언제나 1이어야 함), 웹만 사용") String page,
                                                                                                               @RequestParam(value = "size", required = false, defaultValue = "15") @Schema(description = "입력 안할 경우 기본 15개 반환, 필요시 50개 이하 호출 가능, 그 이상 불가능") String size,
                                                                                                               @RequestParam(value = "lastNo", required = false) @Schema(description = "마지막 게시글 번호") String lastNo,
                                                                                                               @Schema(hidden = true) @ClientPath ClientPathType path,
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
                    "<h3>변경된 점</h3>" +
                    "<p>반환 값이 변경되었습니다. '기존 게시글 수정 완료' String 값이 넘어갔으나 이제는 변경된 내용을 보냅니다.</p>" +
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
                    "</ul>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", ref = "EXPIRED_TOKEN"),
            @ApiResponse(responseCode = "403", ref = "POST_ACCESS_DENIED"),
            @ApiResponse(responseCode = "404-1", ref = "MY_TALENT_KEYWORD_NOT_REGISTERED"),
            @ApiResponse(responseCode = "404-2", ref = "POST_GIVE_MY_TALENT_NOT_FOUND"),
            @ApiResponse(responseCode = "400", ref = "POST_FAILED_UPDATE"),
    })
    public ResponseEntity<CommonResponse<Void>> updateExchangePost(@PathVariable Long postNo, @RequestBody @Valid ExchangePostReqDTO exchangePostReqDTO);

    @Operation(summary = "재능 교환 게시글 삭제",
            description = "<h2>내용</h2>" +
                    "<p>재능 교환 게시글 소프트 삭제입니다.</p>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", ref = "POST_FAILED_DELETE"),
            @ApiResponse(responseCode = "401", ref = "EXPIRED_TOKEN"),
            @ApiResponse(responseCode = "403", ref = "POST_ACCESS_DENIED"),
    })
    public ResponseEntity<CommonResponse<String>> deleteExchangePost(@PathVariable Long postNo, Authentication auth);

    @Operation(summary = "재능 교환 게시글 상태(모집) 변경",
            description = "<h2>내용</h2>" +
                    "<p>재능 교환 게시글 상태 변경입니다.</p>" +
                    "<p>모집중(NOW_RECRUITING), 모집 마감(RECRUITMENT_CLOSED) 상태로 변경 가능합니다.</p>" +
                    "<p>status 데이터가 변경되면서 문제가 발생할 수 있습니다.</p>" +
                    "<p>말씀해주시면 데이터 변경으로 제대로 넘어갈 수 있도록 하겠습니다.</p>" +
                    "<hr>" +
                    "<h2>비동기 방식 요청 제한</h2>" +
                    "<p>1분에 최대 10개의 요청을 보낼 수 있습니다.</p>"+
                    "<hr>" +
                    "<h2>Request</h2>"+
                    "<p>status : 모집중(NOW_RECRUITING), 모집 마감(RECRUITMENT_CLOSED)</p>"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", ref = "POST_FAILED_UPDATE"),
            @ApiResponse(responseCode = "401", ref = "EXPIRED_TOKEN"),
            @ApiResponse(responseCode = "403", ref = "POST_ACCESS_DENIED"),
            @ApiResponse(responseCode = "429", ref = "TOO_MANY_REQUESTS")
    })
    public ResponseEntity<CommonResponse<Void>> patchExchangePostStatus(@PathVariable Long postNo,
                                                                        @RequestBody ExchangePostStatusReqDTO exchangePostStatusReqDTO,
                                                                        Authentication auth);

}



