package com.talearnt.admin.notice;


import com.talearnt.admin.notice.repository.NoticeQueryRepository;
import com.talearnt.admin.notice.response.NoticeListResDTO;
import com.talearnt.admin.notice.response.NoticeListToWebResDTO;
import com.talearnt.util.common.PageUtil;
import com.talearnt.util.common.PostUtil;
import com.talearnt.util.pagination.PagedListWrapper;
import com.talearnt.util.response.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class NoticeService {


    private final NoticeQueryRepository noticeQueryRepository;

    public PaginatedResponse<List<NoticeListResDTO>> getNoticeList(String path, String page, String size) {
        log.info("공지사항 목록 불러오기 시작 : path - {}, page - {}, size - {}", path, page, size);

        Pageable pageable = PostUtil.filterValidPagination(page, size);

        //웹 공지사항 목록 가져오기
        if ("web".equalsIgnoreCase(path)) {
            //웹 공지사항 목록 조회
            PagedListWrapper<NoticeListResDTO> wrapper = noticeQueryRepository.getNoticeListToWeb(pageable);
            //페이지 정보로 변환
            Page<NoticeListResDTO> result = new PageImpl<>(wrapper.getList(),pageable, wrapper.getPagedData().getTotal());

            log.info("공지사항 목록 불러오기 완료 - 웹");
            //반환
            return new PaginatedResponse<>(result.getContent(), PageUtil.separatePaginationFromEntityToWeb(result, wrapper.getPagedData().getLatestCreatedAt()));
        }

        //모바일 공지사항 목록 가져오기
        Page<NoticeListResDTO> result = noticeQueryRepository.getNoticeListToMobile(pageable);

        log.info("공지사항 목록 불러오기 완료 - 모바일");
        return new PaginatedResponse<>(result.getContent(), PageUtil.separatePaginationFromEntityToMobile(result));
    }

}
