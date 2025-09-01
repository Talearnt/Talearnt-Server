package com.talearnt.admin.notice;


import com.talearnt.admin.notice.entity.Notice;
import com.talearnt.admin.notice.repository.NoticeQueryRepository;
import com.talearnt.admin.notice.repository.NoticeRepository;
import com.talearnt.admin.notice.request.NoticeInsertReqDTO;
import com.talearnt.admin.notice.response.NoticeDetailResDTO;
import com.talearnt.admin.notice.response.NoticeListResDTO;
import com.talearnt.admin.notice.response.NoticeListToWebResDTO;
import com.talearnt.enums.common.ErrorCode;
import com.talearnt.enums.user.UserRole;
import com.talearnt.util.common.PageUtil;
import com.talearnt.util.common.PostUtil;
import com.talearnt.util.exception.CustomRuntimeException;
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
    private final NoticeRepository noticeRepository;

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

    public NoticeDetailResDTO getNoticeDetail(Long noticeNo) {
        log.info("공지사항 상세 불러오기 시작 : noticeNo - {}", noticeNo);

        Notice notice = noticeRepository.findById(noticeNo).
                orElseThrow(() -> {
                    log.error("공지사항 상세 불러오기 실패 - 공지사항 없음 : {}, {}", noticeNo, ErrorCode.POST_NOT_FOUND);
                    return new CustomRuntimeException(ErrorCode.POST_NOT_FOUND);
                });

        NoticeDetailResDTO result = NoticeDetailResDTO.builder()
                .noticeNo(notice.getNoticeNo())
                .title(notice.getTitle())
                .content(notice.getContent())
                .noticeType(notice.getNoticeType())
                .createdAt(notice.getCreatedAt())
                .build();

        log.info("공지사항 상세 불러오기 완료");
        return result;
    }

    public void createNotice(NoticeInsertReqDTO noticeInsertReqDTO) {
        log.info("공지사항 생성 시작 - {}", noticeInsertReqDTO);

        if (!noticeInsertReqDTO.getUserInfo().getAuthority().isHigherOrEqual(UserRole.ROLE_MANAGER)) {
            log.error("공지사항 생성 실패 - 권한 없음 : {}, {}", noticeInsertReqDTO.getUserInfo().getUserNo(), ErrorCode.ACCESS_DENIED);
            throw new CustomRuntimeException(ErrorCode.ACCESS_DENIED);
        }

        Notice notice = new Notice();
        notice.setTitle(noticeInsertReqDTO.getTitle());
        notice.setContent(noticeInsertReqDTO.getContent());
        notice.setNoticeType(noticeInsertReqDTO.getNoticeType());
        notice.setCreatedBy(noticeInsertReqDTO.getUserInfo().getUserNo());


        noticeRepository.save(notice);

        log.info("공지사항 생성 완료");
    }

}
