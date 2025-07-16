package com.talearnt.admin.event;


import com.talearnt.admin.event.repository.EventQueryRepository;
import com.talearnt.admin.event.response.EventListResDTO;
import com.talearnt.admin.notice.response.NoticeListResDTO;
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
public class EventService {

    private final EventQueryRepository eventQueryRepository;

    public PaginatedResponse<List<EventListResDTO>> getEventList(String path, String page, String size) {
        log.info("이벤트 목록 불러오기 시작 : path - {}, page - {}, size - {}", path, page, size);

        Pageable pageable = PostUtil.filterValidPagination(page, size);

        if ("web".equalsIgnoreCase(path)) {
            //웹 이벤트 목록 조회
            PagedListWrapper<EventListResDTO> wrapper = eventQueryRepository.getEventListToWeb(pageable);
            //페이지 정보로 변환
            Page<EventListResDTO> result = new PageImpl<>(wrapper.getList(),pageable, wrapper.getPagedData().getTotal());

            log.info("이벤트 목록 불러오기 완료 - 웹");
            //반환
            return new PaginatedResponse<>(result.getContent(), PageUtil.separatePaginationFromEntityToWeb(result, wrapper.getPagedData().getLatestCreatedAt()));
        }
        //모바일 이벤트 목록 가져오기
        Page<EventListResDTO> result = eventQueryRepository.getEventListToMobile(pageable);

        log.info("이벤트 목록 불러오기 완료 - 모바일");
        return new PaginatedResponse<>(result.getContent(), PageUtil.separatePaginationFromEntityToMobile(result));
    }

}
