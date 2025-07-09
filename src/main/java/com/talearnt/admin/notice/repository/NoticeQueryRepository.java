package com.talearnt.admin.notice.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.talearnt.admin.notice.entity.QNotice;
import com.talearnt.admin.notice.response.NoticeListResDTO;
import com.talearnt.admin.notice.response.NoticeListToWebResDTO;
import com.talearnt.post.community.response.CommunityPostListResDTO;
import com.talearnt.util.pagination.PagedData;
import com.talearnt.util.pagination.PagedListWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
@Log4j2
public class NoticeQueryRepository {

    private final JPAQueryFactory factory;

    private final QNotice notice = QNotice.notice;


    public PagedListWrapper<NoticeListResDTO> getNoticeListToWeb(Pageable pageable) {
        List<NoticeListResDTO> data = getSelectedList("web")
                .from(notice)
                .orderBy(notice.noticeNo.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        PagedData pagedData = factory.select(Projections.constructor(PagedData.class,
                        notice.count(),
                        Expressions.dateTemplate(LocalDateTime.class,
                                "MAX({0})",
                                notice.createdAt)))
                .from(notice)
                .fetchOne();

        return PagedListWrapper.<NoticeListResDTO>builder().list(data).pagedData(pagedData).build();
    }


    public Page<NoticeListResDTO> getNoticeListToMobile(Pageable pageable) {
        List<NoticeListResDTO> data = getSelectedList("mobile")
                .from(notice)
                .orderBy(notice.noticeNo.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = Optional.ofNullable(
                factory.select(notice.count())
                        .from(notice)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(data, pageable, total);
    }


    private JPAQuery<NoticeListResDTO> getSelectedList(String path) {
        JPAQuery<NoticeListResDTO> selected = null;
        if ("web".equalsIgnoreCase(path)) {
            selected = factory.select(Projections.constructor(NoticeListToWebResDTO.class,
                    notice.noticeNo,
                    notice.title,
                    notice.noticeType,
                    notice.createdAt,
                    notice.content
            ));
        } else {
            selected = factory.select(Projections.constructor(NoticeListResDTO.class,
                    notice.noticeNo,
                    notice.title,
                    notice.noticeType,
                    notice.createdAt
            ));
        }
        return selected;
    }

}
