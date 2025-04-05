package com.talearnt.reply.community;

import com.talearnt.reply.community.repository.ReplyQueryRepository;
import com.talearnt.reply.community.request.ReplySearchCondition;
import com.talearnt.reply.community.response.ReplyListResDTO;
import com.talearnt.util.common.PageUtil;
import com.talearnt.util.response.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class ReplyService {

    private final ReplyQueryRepository replyQueryRepository;

    /**
     * 커뮤니티 답글 목록을 가져옵니다.
     * 조건 없음)
     * @param commentNo - 댓글 번호
     * @param lastNo - 마지막 답글 번호 번호
     * @param page - 페이지 번호
     * @param size - 페이지 사이즈
     * @param path - 접근 경로 (web, mobile)
     */
    public PaginatedResponse<List<ReplyListResDTO>> getReplies(Long commentNo,
                                                               String lastNo,
                                                               String page,
                                                               String size) {
        log.info("커뮤니티 답글 목록 시작 commentNo : {}, lastNo : {}, page : {}, size : {}", commentNo, lastNo, page, size);

        //Condition 생성
        ReplySearchCondition condition = ReplySearchCondition.builder()
                .lastNo(lastNo)
                .page(page)
                .size(size)
                .build();

        //조건에 맞는 답글 목록 조회
        Page<ReplyListResDTO> result = replyQueryRepository.getReplies(commentNo, condition);

        log.info("커뮤니티 답글 목록 끝");
        return new PaginatedResponse<>(result.getContent(), PageUtil.separatePaginationFromEntityToMobile(result));
    }

}
