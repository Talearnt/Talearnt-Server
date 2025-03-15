package com.talearnt.comment.community;


import com.talearnt.comment.community.repository.CommentRepository;
import com.talearnt.comment.community.request.CommentReqDTO;
import com.talearnt.enums.common.ErrorCode;
import com.talearnt.post.community.repository.CommunityPostQueryRepository;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.log.LogRunningTime;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommunityPostQueryRepository communityPostQueryRepository;

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




        log.info("커뮤니티 게시글 댓글 추가 끝");
        return null;
    }
}
