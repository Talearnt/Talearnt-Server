package com.talearnt.admin.event.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
@ToString
public class EventListResDTO {
    private Long eventNo;
    private String bannerUrl;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;
}
