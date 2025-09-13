package com.talearnt.admin.event.repository;


import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.talearnt.admin.event.entity.QEvent;
import com.talearnt.admin.event.response.EventBannerListResDTO;
import com.talearnt.admin.event.response.EventListResDTO;
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
public class EventQueryRepository {

    private final JPAQueryFactory factory;
    private final QEvent event = QEvent.event;

    public List<EventBannerListResDTO> getEventBannerList() {
        return factory.select(
                        Projections.constructor(EventBannerListResDTO.class,
                                event.eventNo,
                                event.bannerUrl
                        )
                )
                .from(event)
                .where(
                        event.startDate.loe(LocalDateTime.now()) // 시작일이 현재 시간보다 작거나 같고
                                .and(
                                        event.endDate.isNull() // endDate 가 null 이거나
                                                .or(event.endDate.goe(LocalDateTime.now())) // endDate 가 현재 시간보다 크거나 같을 때
                                )
                )
                .orderBy(event.eventNo.desc())
                .fetch();
    }


    public Page<EventListResDTO> getEventListToMobile(Pageable pageable) {
        List<EventListResDTO> data = getSelectedList()
                .from(event)
                .orderBy(new CaseBuilder()
                                .when(event.startDate.loe(LocalDateTime.now())// 시작일보다 크거나 같고
                                        .and(
                                                event.endDate.isNull() // endDate null 이거나
                                                        .or(event.endDate.goe(LocalDateTime.now())) // endDate 가 현재 시간보다 크거나 같을 때
                                        )) // 종료일보다 작거나 같을 때
                                .then(1)
                                .otherwise(0)
                                .desc(), // 현재 진행중인 이벤트가 가장 위로 오도록 정렬
                        event.eventNo.desc()) // 이벤트 번호로 내림차순 정렬
                .offset(pageable.getOffset()) // 페이지 오프셋 설정
                .limit(pageable.getPageSize()) // 페이지 크기 설정
                .fetch();


        long total = Optional.ofNullable(
                factory.select(event.count())
                        .from(event)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(data, pageable, total);
    }


    public PagedListWrapper<EventListResDTO> getEventListToWeb(Pageable pageable) {
        List<EventListResDTO> data = getSelectedList()
                .from(event)
                .orderBy(new CaseBuilder()
                                .when(event.startDate.loe(LocalDateTime.now())// 시작일보다 크거나 같고
                                        .and(
                                            event.endDate.isNull() // endDate 가 null 이거나
                                                .or(event.endDate.goe(LocalDateTime.now())) // endDate 가 현재 시간보다 크거나 같을 때
                                        )) // 종료일보다 작거나 같을 때
                                .then(1)
                                .otherwise(0)
                                .desc(), // 현재 진행중인 이벤트가 가장 위로 오도록 정렬
                        event.eventNo.desc()) // 이벤트 번호로 내림차순 정렬
                .offset(pageable.getOffset()) // 페이지 오프셋 설정
                .limit(pageable.getPageSize()) // 페이지 크기 설정
                .fetch();

        PagedData pagedData = factory.select(Projections.constructor(PagedData.class,
                        event.count(),
                        Expressions.dateTemplate(LocalDateTime.class,
                                "MAX({0})",
                                event.createdAt)))
                .from(event)
                .fetchOne();

        return PagedListWrapper.<EventListResDTO>builder()
                .list(data)
                .pagedData(pagedData)
                .build();
    }

    private JPAQuery<EventListResDTO> getSelectedList() {
        return factory.select(Projections.constructor(EventListResDTO.class,
                event.eventNo,
                new CaseBuilder()
                        .when(event.endDate.loe(LocalDateTime.now()))
                        .then(event.endedBannerUrl)
                        .otherwise(event.bannerUrl),
                event.startDate,
                event.endDate,
                new CaseBuilder()
                        .when(event.startDate.loe(LocalDateTime.now()) // 현재 시간보다 시작일이 작거나 같고
                                .and(
                                        event.endDate.isNull() // endDate 가 null 이거나
                                                .or(event.endDate.goe(LocalDateTime.now())) // endDate 가 현재 시간보다 크거나 같을 때
                                ))// 종료일이 현재 시간보다 크거나 같을 때
                        .then(true)
                        .otherwise(false)));
    }

}
