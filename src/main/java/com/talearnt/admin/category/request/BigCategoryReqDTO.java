package com.talearnt.admin.category.request;

import com.talearnt.enums.common.ErrorCode;
import com.talearnt.enums.common.Regex;
import com.talearnt.util.common.RequiredJwtValueDTO;
import com.talearnt.util.jwt.UserInfo;
import com.talearnt.util.valid.DynamicValid;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/* 대분류 카테고리 추가를 위한 DTO입니다.
 * 관리자 페이지에서 관리자가 대분류를 추가할 때 사용합니다.
 * UserInfo는 관리자의 아이디를 저장하기 위해 추가했습니다. (무분별 추가 시 누가 추가했는지 관리 용도에서 사용하기 위해 추가했습니다)
 * Category Name은 Unique입니다.
 * IT가 있을 경우 IT는 추가 되지 않습니다.*/

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RequiredJwtValueDTO
public class BigCategoryReqDTO {
    @Schema(hidden = true)
    private UserInfo userInfo;
    @DynamicValid(errorCode = ErrorCode.KEYWORD_CODE_MISMATCH, pattern = Regex.CATEGORY_CODE)
    private Integer categoryCode;
    @DynamicValid(errorCode = ErrorCode.KEYWORD_NAME_MISMATCH, pattern = Regex.CATEGORY_NAME)
    private String categoryName;
}
