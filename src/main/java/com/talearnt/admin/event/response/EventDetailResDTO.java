package com.talearnt.admin.event.response;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
public class EventDetailResDTO {
    private Long eventNo;
    private String title;
    private String content;
    private String bannerUrl;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private Boolean isActive;
}
