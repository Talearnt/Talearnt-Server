package com.talearnt.admin.category;

import com.talearnt.admin.category.request.BigCategoryReqDTO;
import com.talearnt.admin.category.request.TalentCategoryReqDTO;
import com.talearnt.admin.category.service.CategoryService;
import com.talearnt.admin.category.service.TalentCategoryService;
import com.talearnt.util.response.CommonResponse;
import com.talearnt.util.version.RestAdminControllerV1;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestAdminControllerV1
@RequiredArgsConstructor
@Tag(name = "관리자 1. 재능 키워드", description = "관리자 페이지의 키워드 관리")
public class CategoryController {

    private final CategoryService categoryService;
    private final TalentCategoryService talentCategoryService;

    @Operation(summary = "대분류 1. 키워드 추가", description = "대분류 키워드를 추가합니다. (관리자 권한 - 아직 미구현)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400-1", ref = "KEYWORD_CODE_MISMATCH"),
            @ApiResponse(responseCode = "400-2", ref = "KEYWORD_NAME_MISMATCH"),
            @ApiResponse(responseCode = "400-3", ref = "KEYWORD_CODE_DUPLICATION"),
            @ApiResponse(responseCode = "400-4", ref = "KEYWORD_NAME_DUPLICATION"),
    })
    @PostMapping("/keywords/big-categories")
    public ResponseEntity<CommonResponse<String>> addBigCategoryKeyword(@RequestBody @Valid BigCategoryReqDTO bigCategoryReqDTO){
        return CommonResponse.success(categoryService.addBigCategory(bigCategoryReqDTO));
    }


    @Operation(summary = "재능 분류 1. 키워드 추가", description = "재능 분류 키워드를 추가합니다. (관리자 권한 - 미구현)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400-1", ref = "KEYWORD_CODE_MISMATCH"),
            @ApiResponse(responseCode = "400-2", ref = "KEYWORD_NAME_MISMATCH"),
            @ApiResponse(responseCode = "400-3", ref = "KEYWORD_CATEGORY_CODE_MISMATCH"),
            @ApiResponse(responseCode = "400-4", ref = "KEYWORD_TALENT_CODE_DUPLICATION"),
            @ApiResponse(responseCode = "400-5", ref = "KEYWORD_TALENT_NAME_DUPLICATION"),
    })
    @PostMapping("/keywords/talent-categories")
    public ResponseEntity<CommonResponse<String>> addTalenrtCategoryKeyword(@RequestBody @Valid TalentCategoryReqDTO talentCategoryReqDTO){
        return CommonResponse.success(talentCategoryService.addTalentCategoryKeyword(talentCategoryReqDTO));
    }

}
