package com.talearnt.admin.agree.response;


import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AgreeMarketingAndAdvertisingResDTO {
    private boolean isAdvertising;
    private boolean isMarketing;
}
