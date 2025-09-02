package com.talearnt.admin.event;


import com.talearnt.admin.event.entity.Event;
import com.talearnt.admin.event.repository.EventQueryRepository;
import com.talearnt.admin.event.repository.EventRepository;
import com.talearnt.admin.event.request.EventInsertReqDTO;
import com.talearnt.admin.event.response.EventDetailResDTO;
import com.talearnt.admin.event.response.EventListResDTO;
import com.talearnt.admin.notice.response.NoticeListResDTO;
import com.talearnt.enums.common.ErrorCode;
import com.talearnt.enums.user.UserRole;
import com.talearnt.util.common.PageUtil;
import com.talearnt.util.common.PostUtil;
import com.talearnt.util.exception.CustomRuntimeException;
import com.talearnt.util.log.LogRunningTime;
import com.talearnt.util.pagination.PagedListWrapper;
import com.talearnt.util.response.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class EventService {

    private final EventQueryRepository eventQueryRepository;
    private final EventRepository eventRepository;


    @LogRunningTime
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


    @LogRunningTime
    public EventDetailResDTO getEventDetail(Long eventNo) {
        log.info("이벤트 상세 보기 조회 시작 - eventNo : {}", eventNo);

        Event event = eventRepository.findById(eventNo).orElseThrow(() -> {
            log.error("이벤트 상세 보기 조회 실패 - 존재하지 않는 이벤트 게시글 번호: {}", eventNo);
            return new CustomRuntimeException(ErrorCode.POST_NOT_FOUND);
        });

        EventDetailResDTO eventDetailResDTO = EventDetailResDTO.builder()
                .eventNo(event.getEventNo())
                .content(event.getContent())
                .bannerUrl(event.getBannerUrl())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .createdAt(event.getCreatedAt())
                .isActive(event.getIsActive())
                .build();

        log.info("이벤트 상세 보기 조회 끝 - result : {}", eventDetailResDTO);
        return eventDetailResDTO;
    }

    public void createEvent(EventInsertReqDTO insertReqDTO) {
        log.info("이벤트 작성하기 시작 - insertReqDTO : {}", insertReqDTO);

        //매니저 이상 권한 확인
        if (!insertReqDTO.getUserInfo().getAuthority().isHigherOrEqual(UserRole.ROLE_MANAGER)){
            log.error("이벤트 작성 실패 - 권한 없음 : {}, {}", insertReqDTO.getUserInfo().getUserNo(), ErrorCode.ACCESS_DENIED);
            throw new CustomRuntimeException(ErrorCode.ACCESS_DENIED);
        }

        LocalDateTime now = LocalDateTime.now();

        Event event = new Event();
        event.setContent(insertReqDTO.getContent());
        event.setBannerUrl(insertReqDTO.getBannerUrl());
        event.setStartDate(insertReqDTO.getStartDate());
        event.setEndDate(insertReqDTO.getEndDate());
        event.setCreatedAt(now);
        event.setIsActive(insertReqDTO.getStartDate() != null &&insertReqDTO.getEndDate() != null &&
                        !now.isBefore(insertReqDTO.getStartDate()) && now.isBefore(insertReqDTO.getEndDate())
        );
        event.setCreatedBy(insertReqDTO.getUserInfo().getUserNo());
        eventRepository.save(event);

        log.info("이벤트 작성하기 끝");

    }

}
