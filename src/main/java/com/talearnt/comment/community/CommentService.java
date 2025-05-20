package com.talearnt.comment.community;


import com.talearnt.comment.community.entity.CommunityComment;
import com.talearnt.comment.community.repository.CommentQueryRepository;
import com.talearnt.comment.community.repository.CommentRepository;
import com.talearnt.comment.community.request.CommentSearchCondition;
import com.talearnt.comment.community.response.CommentListResDTO;
import com.talearnt.enums.common.ErrorCode;
import com.talearnt.post.community.repository.CommunityPostQueryRepository;
import com.talearnt.util.common.PageUtil;
import com.talearnt.util.common.UserUtil;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.jwt.UserInfo;
import com.talearnt.util.log.LogRunningTime;
import com.talearnt.util.pagination.PagedListWrapper;
import com.talearnt.util.response.PaginatedResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommunityPostQueryRepository communityPostQueryRepository;
    private final CommentQueryRepository commentQueryRepository;



    @LogRunningTime
    public PaginatedResponse<List<CommentListResDTO>> getCommunityComments(Long communityPostNo,String path, String deletedAt,String lastNo, String page, String size) {
        log.info("커뮤니티 댓글 목록 조회 시작 : {} - {} - {}",communityPostNo,path,page);

        //Search Condition 생성
        CommentSearchCondition condition = CommentSearchCondition.builder()
                .lastNo(lastNo)
                .deletedAt(deletedAt)
                .page(page)
                .size(size)
                .build();

        // path로 웹 OR Mobile 분기
        if ("web".equalsIgnoreCase(path)) {
            if (lastNo != null) {
                log.error("커뮤니티 댓글 목록 조회 실패 - LastNo가 포함되어 제대로 된 값 호출 X : {} - {}",communityPostNo ,ErrorCode.COMMENT_FAILED_CALL_LIST);
                throw new CustomRuntimeException(ErrorCode.COMMENT_FAILED_CALL_LIST);
            }
            //댓글 목록 조회
            PagedListWrapper<CommentListResDTO> wrapper =commentQueryRepository.getCommentListToWeb(communityPostNo,condition);

            //Page로 값 변환
            Page<CommentListResDTO> result = new PageImpl<>(wrapper.getList(), condition.getPage(), wrapper.getPagedData().getTotal());

            log.info("커뮤니티 댓글 목록 조회 끝 - {}",path);
            return new PaginatedResponse<>(result.getContent(), PageUtil.separatePaginationFromEntityToWeb(result,wrapper.getPagedData().getLatestCreatedAt()));
        }

        if (condition.getPage().getPageNumber() > 0){
            log.error("커뮤니티 댓글 목록 조회 실패 - Page가 2 이상이어서 제대로 된 값 호출 X : {} - {}",communityPostNo ,ErrorCode.COMMENT_FAILED_CALL_LIST);
            throw new CustomRuntimeException(ErrorCode.COMMENT_FAILED_CALL_LIST);
        }

        //댓글 목록 조회 - 모바일
        Page<CommentListResDTO> result = commentQueryRepository.getCommentListToMobile(communityPostNo, condition, path);

        log.info("커뮤니티 댓글 목록 조회 끝 - {}",path);
        return new PaginatedResponse<>(result.getContent(), PageUtil.separatePaginationFromEntityToMobile(result));
    }

    /** 커뮤니티 게시글 댓글 등록
     * 조건 )
     * - 로그인이 되어있는가?
     * - Valid를 통과했는가?
     * - 댓글 달 커뮤니티 게시글이 활성화 되어 있는가?
     * - 댓글 작성 후 목록 반환 조건
     * */
    @LogRunningTime
    @Transactional
    public PaginatedResponse<List<CommentListResDTO>> addComment(Long userNo, Long communityPostNo, String content, String path) {
        log.info("커뮤니티 게시글 댓글 추가 시작 : {}", communityPostNo);

        //커뮤니티 게시글이 있는지 조회 있을 경우 : true 반환, 없을 경우 false 반환
        if (!communityPostQueryRepository.existCommunityByPostNo(communityPostNo)){
            log.error("커뮤니티 게시글 댓글 추가 실패 - 해당 게시글이 삭제되었거나 없음 : {} - {}", communityPostNo, ErrorCode.COMMENT_NOT_FOUND_POST);
            throw new CustomRuntimeException(ErrorCode.COMMENT_NOT_FOUND_POST);
        }

        //Entity로 변환
        CommunityComment comment = CommentMapper.INSTANCE.toEntity(userNo, communityPostNo, content);

        //댓글 저장
        comment = commentRepository.save(comment);

        /*웹은 마지막 페이지에 있는 게시글을 보여줘야 한다.
        * 보여줘야 하는 댓글 갯수는 30개 오래된 순으로 가져오도록 한다.*/
        if ("web".equalsIgnoreCase(path)) {
            //게시글 총 갯수 가져오기
            Long commentTotalCount = commentQueryRepository.getCommentTotalCount(communityPostNo);
            
            //사이즈 설정
            int size = 30;
            
            //마지막 페이지 설정 == 토탈 페이지 수가 마지막 수인 것을 알 수 있다.
            int totalPage = (int) Math.ceil((double) commentTotalCount / size);
            
            //Search Condition 생성
            CommentSearchCondition condition = CommentSearchCondition.builder()
                    .page(Integer.toString(totalPage))
                    .size(Integer.toString(size))
                    .build();
            
            // 목록 데이터 가져오기
            PagedListWrapper<CommentListResDTO> wrapper = commentQueryRepository.getCommentListToWeb(communityPostNo, condition);

            //Page로 값 변환
            Page<CommentListResDTO> result = new PageImpl<>(wrapper.getList(), condition.getPage(), wrapper.getPagedData().getTotal());

            return new PaginatedResponse<>(result.getContent(), PageUtil.separatePaginationFromEntityToWeb(result,wrapper.getPagedData().getLatestCreatedAt()));

        }
        /*모바일은 최신 댓글 목록을 보여준다. 30개를 가져오되 최신순으로 가져오고 Sorted로 오래된 순으로 변경하고 보내준다.*/
        //Condition 조건 설정
        CommentSearchCondition condition = CommentSearchCondition.builder().page("1").size("30").build();

        //댓글 목록 조회 - 모바일
        Page<CommentListResDTO> result = commentQueryRepository.getCommentListToMobile(communityPostNo, condition, path);

        log.info("커뮤니티 게시글 댓글 추가 끝");
        return new PaginatedResponse<>(result.getContent(), PageUtil.separatePaginationFromEntityToMobile(result));
    }

    /**커뮤니티 게시글 댓글 수정
     * 조건)
     * - 로그인 했는가?
     * - 나의 댓글이 맞고, 삭제한 댓글이 아닌가?
     * */
    @Transactional
    @LogRunningTime
    public Void updateComment(Long userNo, Long commentNo, String content) {
        log.info("커뮤니티 게시글 댓글 수정 시작 : {} - {} - {}", userNo, commentNo, content);

        //나의 댓글이 맞고, 삭제한 댓글이 아닌가?
        if (!commentQueryRepository.isMyCommentAndIsNotDeleted(userNo, commentNo)){
            log.error("커뮤니티 게시글 댓글 수정 실패 - 삭제된 댓글을 수정하려하거나, 나의 댓글이 아님 : {} - {}",commentNo,ErrorCode.COMMENT_ACCESS_DINED);
            throw new CustomRuntimeException(ErrorCode.COMMENT_ACCESS_DINED);
        }

        Long updatedCount = commentQueryRepository.updateCommentByUserNoAndCommentNo(userNo, commentNo, content);

        //0개 또는 여러 개 업데이트 될 경우 버그 발생으로 Throw 발생
        if (updatedCount != 1){
            log.error("커뮤니티 게시글 댓글 수정 실패 - 0개 또는 여러 개의 댓글이 수정되었습니다 : {} - {}", updatedCount, ErrorCode.COMMENT_FAILED_UPDATE);
            throw new CustomRuntimeException(ErrorCode.COMMENT_FAILED_UPDATE);
        }

        log.info("커뮤니티 게시글 댓글 수정 끝");
        return null;
    }

    /** 커뮤니티 게시글 댓글 삭제
     * 조건 )
     * - 로그인을 했는가?
     * - 나의 댓글이 맞고, 삭제한 게시글이 아닌가?
     * */
    @Transactional
    @LogRunningTime
    public Void deleteComment(Authentication authentication, Long commentNo) {
        log.info("커뮤니티 게시글 댓글 삭제 시작 : {}", commentNo);

        //로그인 여부 확인
        UserInfo userInfo = UserUtil.validateAuthentication("커뮤니티 게시글 댓글 삭제", authentication);

        //나의 댓글이 맞고, 삭제한 게시글이 아닌가?
        if (!commentQueryRepository.isMyCommentAndIsNotDeleted(userInfo.getUserNo(), commentNo)){
            log.error("커뮤니티 게시글 댓글 삭제 실패 - 나의 댓글이 아니거나, 이미 삭제된 게시글 : {} - {}", commentNo, ErrorCode.COMMENT_ACCESS_DINED);
            throw new CustomRuntimeException(ErrorCode.COMMENT_ACCESS_DINED);
        }

        //댓글 소프트 삭제
        Long deletedCount = commentQueryRepository.deleteCommentByUserNoAndCommentNo(userInfo.getUserNo(), commentNo);

        //0개 또는 여러 개 댓글이 삭제되어 Throw 발생
        if (deletedCount != 1){
            log.error("커뮤니티 게시글 댓글 삭제 실패 - 0개 또는 여러 개의 댓글이 삭제되었습니다 : {} - {}", deletedCount, ErrorCode.COMMENT_FAILED_DELETE);
            throw new CustomRuntimeException(ErrorCode.COMMENT_FAILED_DELETE);
        }

        log.info("커뮤니티 게시글 댓글 삭제 끝");
        return null;
    }
}
