package com.talearnt.admin.event.response;

import lombok.*;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
public class EventBannerListResDTO {
    private Long eventNo;
    private String bannerUrl;
}
