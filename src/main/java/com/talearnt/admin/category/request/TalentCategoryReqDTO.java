package com.talearnt.admin.category.request;

import com.talearnt.util.jwt.UserInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/*재능을 추가할 때 사용하는 Request DTO 입니다.
* UserInfo는 Binder에서 자동 주입 됩니다.
* userInfo는 JWT 토큰을 분석해서 주입됩니다. 즉, 로그인 하여야 제대로 주입이 됩니다.
* TalentCode는 1단위로 증가하도록 만들어야합니다. 다만, 자동증가는 사용할 수 없도록 만들었습니다.
* 그 이유는 1000번대는 IT 관련, 2000번대는 디자인 관련 이런식으로 코드를 만들 예정이기 때문입니다.
* 1001은 JAVA, 1002는 C, 1003은 파이썬 이런 형태입니다.*/

@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TalentCategoryReqDTO {
    @Schema(hidden = true)
    private UserInfo userInfo;
    private Integer talentCode;
    private Integer categoryCode;
    private String talentName;
}
