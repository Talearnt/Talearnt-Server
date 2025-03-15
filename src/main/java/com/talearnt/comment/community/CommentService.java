package com.talearnt.comment.community;


import com.talearnt.comment.community.entity.CommunityComment;
import com.talearnt.comment.community.repository.CommentQueryRepository;
import com.talearnt.comment.community.repository.CommentRepository;
import com.talearnt.comment.community.request.CommentReqDTO;
import com.talearnt.comment.community.request.CommentSearchCondition;
import com.talearnt.comment.community.response.CommentListResDTO;
import com.talearnt.enums.common.ErrorCode;
import com.talearnt.post.community.repository.CommunityPostQueryRepository;
import com.talearnt.util.common.PageUtil;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.log.LogRunningTime;
import com.talearnt.util.pagination.PagedListWrapper;
import com.talearnt.util.response.PaginatedResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    public PaginatedResponse<List<CommentListResDTO>> getCommunityComments(Long communityPostNo, String path, String lastNo, String page, String size) {
        log.info("커뮤니티 댓글 목록 조회 시작 : {} - {} - {}",communityPostNo,path,page);

        //Search Condition 생성
        CommentSearchCondition condition = CommentSearchCondition.builder()
                .lastNo(lastNo)
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
        Page<CommentListResDTO> result = commentQueryRepository.getCommentListToMobile(communityPostNo, condition);

        log.info("커뮤니티 댓글 목록 조회 끝 - {}",path);
        return new PaginatedResponse<>(result.getContent(), PageUtil.separatePaginationFromEntityToMobile(result));
    }

    /** 커뮤니티 게시글 댓글 등록
     * 조건 )
     * - 로그인이 되어있는가?
     * - Valid를 통과했는가?
     * - 댓글 달 커뮤니티 게시글이 활성화 되어 있는가?
     * */
    @LogRunningTime
    @Transactional
    public Long addComment(CommentReqDTO commentReqDTO) {
        log.info("커뮤니티 게시글 댓글 추가 시작 : {}", commentReqDTO);

        //커뮤니티 게시글이 있는지 조회 있을 경우 : true 반환, 없을 경우 false 반환
        if (!communityPostQueryRepository.existCommunityByPostNo(commentReqDTO.getCommunityPostNo())){
            log.error("커뮤니티 게시글 댓글 추가 실패 - 해당 게시글이 삭제되었거나 없음 : {} - {}", commentReqDTO.getCommunityPostNo(), ErrorCode.COMMENT_NOT_FOUND_POST);
            throw new CustomRuntimeException(ErrorCode.COMMENT_NOT_FOUND_POST);
        }

        //Entity로 변환
        CommunityComment comment = CommentMapper.INSTANCE.toEntity(commentReqDTO);

        //댓글 저장
        comment = commentRepository.save(comment);

        log.info("커뮤니티 게시글 댓글 추가 끝");
        return comment.getCommentNo();
    }
}
