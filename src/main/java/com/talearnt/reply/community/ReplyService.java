package com.talearnt.reply.community;

import com.talearnt.comment.community.repository.CommentRepository;
import com.talearnt.enums.common.ErrorCode;
import com.talearnt.reply.community.entity.CommunityReply;
import com.talearnt.reply.community.repository.ReplyQueryRepository;
import com.talearnt.reply.community.repository.ReplyRepository;
import com.talearnt.reply.community.request.ReplySearchCondition;
import com.talearnt.reply.community.response.ReplyListResDTO;
import com.talearnt.user.infomation.entity.User;
import com.talearnt.user.infomation.repository.UserRepository;
import com.talearnt.util.common.PageUtil;
import com.talearnt.util.common.UserUtil;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.jwt.UserInfo;
import com.talearnt.util.log.LogRunningTime;
import com.talearnt.util.response.PaginatedResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class ReplyService {

    private final UserRepository userRepository;
    private final ReplyQueryRepository replyQueryRepository;
    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;

    /**
     * 커뮤니티 답글 목록을 가져옵니다.<br>
     * 조건 없음)<br>
     *
     * @param commentNo - 댓글 번호
     * @param lastNo    - 마지막 답글 번호 번호
     * @param size      - 페이지 사이즈
     */
    @LogRunningTime
    public PaginatedResponse<List<ReplyListResDTO>> getReplies(Long commentNo,
                                                               String lastNo,
                                                               String size) {
        log.info("커뮤니티 답글 목록 시작 commentNo : {}, lastNo : {}, size : {}", commentNo, lastNo, size);

        //Condition 생성
        ReplySearchCondition condition = ReplySearchCondition.builder()
                .lastNo(lastNo)
                .page("1")
                .size(size)
                .build();

        //조건에 맞는 답글 목록 조회
        Page<ReplyListResDTO> result = replyQueryRepository.getReplies(commentNo, condition);

        log.info("커뮤니티 답글 목록 끝");
        return new PaginatedResponse<>(result.getContent(), PageUtil.separatePaginationFromEntityToMobile(result));
    }

    /**
     * 커뮤니티 답글 작성<br>
     * 조건 ) <br>
     * - 로그인 여부 확인 (DTO에서 확인) <br>
     * - 댓글 번호 확인 <br>
     *
     * @param userNo    - 회원 번호
     * @param commentNo - 부모 댓글 번호
     * @param content   - 답글 내용
     * @return 최신화된 답글 목록
     */
    @LogRunningTime
    @Transactional
    public ReplyListResDTO createReply(Long userNo, Long commentNo, String content) {
        log.info("커뮤니티 답글 작성 시작 , userNo : {}, commentNo : {}, content : {}", userNo, commentNo, content);

        //댓글 번호 확인
        if (!commentRepository.existsById(commentNo)) {
            log.error("커뮤니티 답글 작성 실패 - 댓글 번호 없음, commentNo : {}", commentNo);
            throw new CustomRuntimeException(ErrorCode.COMMENT_MISMATCH_REPLY_NUMBER);
        }

        User user = userRepository.findByUserNo(userNo)
                .orElseThrow(() -> new CustomRuntimeException(ErrorCode.USER_NOT_FOUND));

        //Entity로 변환
        CommunityReply reply = ReplyMapper.INSTANCE.toEntity(user, commentNo, content);

        //답글 작성
        CommunityReply createdReply = replyRepository.save(reply);

        log.info("커뮤니티 답글 작성 끝 : {}", createdReply);
        return ReplyListResDTO
                .builder()
                .userNo(createdReply.getUser().getUserNo())
                .nickname(createdReply.getUser().getNickname())
                .profileImg(createdReply.getUser().getProfileImg())
                .replyNo(createdReply.getReplyNo())
                .commentNo(createdReply.getCommunityComment().getCommentNo())
                .content(createdReply.getContent())
                .createdAt(createdReply.getCreatedAt())
                .build();
    }

    /**
     * 커뮤니티 답글 수정
     * 조건 )
     * - 로그인 했는가?
     * - 게시글이 존재하는가?
     * - 나의 게시글이 맞는가?
     *
     * @param replyNo - 답글 번호
     */
    @LogRunningTime
    @Transactional
    public Void updateReply(Long userNo, Long replyNo, String content) {
        log.info("커뮤니티 답글 수정 시작 , replyNo : {}, content : {}", replyNo, content);

        //답글 번호 확인
        CommunityReply reply = replyQueryRepository.findByIdAndNotDeleted(replyNo)
                .orElseThrow(() -> {
                    log.error("커뮤니티 답글 수정 실패 - 답글 번호 없음, replyNo : {}", replyNo);
                    return new CustomRuntimeException(ErrorCode.REPLY_NOT_FOUND);
                });

        //나의 게시글이 맞는가 판다
        if (!reply.getUser().getUserNo().equals(userNo)) {
            log.error("커뮤니티 답글 수정 실패 - 나의 답글이 아님, replyNo : {}, userNo : {}", replyNo, userNo);
            throw new CustomRuntimeException(ErrorCode.COMMENT_ACCESS_DINED);
        }

        //답글 내용 수정 더티 체킹
        reply.setContent(content);

        log.info("커뮤니티 답글 수정 끝");
        return null;
    }

    /**
     * 커뮤니티 답글 삭제
     * 조건 )
     * - 로그인 했는가?
     * - 게시글이 존재하는가?
     * - 나의 게시글이 맞는가?
     * - 삭제된 게시글이 아닌가?
     * @param replyNo - 답글 번호
     * @param authentication - Authentication
     * @Return Void
     * */
    @LogRunningTime
    @Transactional
    public Void deleteReply(Long replyNo, Authentication authentication) {
        log.info("커뮤니티 답글 삭제 시작 , replyNo : {}", replyNo);

        //로그인 했는가?
        UserInfo userInfo = UserUtil.validateAuthentication("커뮤니티 답글 삭제", authentication);

        //답글 번호 존재 여부 확인
        CommunityReply reply = replyQueryRepository.findByIdAndNotDeleted(replyNo)
                .orElseThrow(() -> {
                    log.error("커뮤니티 답글 삭제 실패 - 답글 번호 없음, replyNo : {}", replyNo);
                    return new CustomRuntimeException(ErrorCode.REPLY_NOT_FOUND);
                });

        //나의 게시글이 맞는가 판단
        if (!reply.getUser().getUserNo().equals(userInfo.getUserNo())) {
            log.error("커뮤니티 답글 삭제 실패 - 나의 답글이 아님, replyNo : {}, userNo : {}", replyNo, userInfo.getUserNo());
            throw new CustomRuntimeException(ErrorCode.COMMENT_ACCESS_DINED);
        }

        //답글 삭제 더티 체킹
        reply.setDeletedAt(LocalDateTime.now());

        log.info("커뮤니티 답글 삭제 끝");
        return null;
    }
}
